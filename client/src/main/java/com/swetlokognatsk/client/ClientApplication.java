package com.swetlokognatsk.client;

import java.io.IOException;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.Thymeleaf;
import com.swetlokognatsk.client.infrastructure.Database;
import com.swetlokognatsk.client.model.AccessToken;
import com.swetlokognatsk.client.model.RefreshAndAccessTokensPair;
import com.swetlokognatsk.client.model.RefreshToken;
import com.swetlokognatsk.client.model.Token;
import com.swetlokognatsk.client.model.TokenStrategy;
import com.swetlokognatsk.client.services.AppHttpClient;
import com.swetlokognatsk.client.services.Service;
import com.swetlokognatsk.client.services.TokenParser;
import com.swetlokognatsk.client.services.URIBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import static com.swetlokognatsk.client.model.TokenStrategy.*;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Controller
public class ClientApplication {

	private static final String SING_IN_VIA_GIP_HUB_PATH = "/sing-in-via-gip-hub";
	private static final String FETCH_PROTECTED_RESOURCE_PATH = "/fetch-protected-resource";

	private static ApplicationContext ctx;
	private static final String SESSION_COOKIE = "CUSTOM_SESSION";

	private static final String STATE = "state";
	private static final String TOKEN = "token";

	private static final String TOKEN_STRATEGY = "TOKEN_STRATEGY";

	private final Service service;
	private final MapSessionRepository sessionRepository;

	public static void main(String[] args) {
		ctx = SpringApplication.run(ClientApplication.class, args);
	}

	public ClientApplication(final Service service, final MapSessionRepository sessionRepository) {
		this.service = service;
		this.sessionRepository = sessionRepository;
	}

	@GetMapping("/")
	public ModelAndView home(final Model model, @CookieValue(SESSION_COOKIE) String sessionId, final HttpServletResponse response) {
		sessionId = ensureSessionExists(sessionId, response);

		String state = getState(sessionId);
		model.addAttribute("state", state);

		Token token = getToken(sessionId);
		model.addAttribute("token", token);

		model.addAttribute("scope", null);

		model.addAttribute("getAuthEndpointUri", SING_IN_VIA_GIP_HUB_PATH);

		model.addAttribute("askForProtectedResourceUri", FETCH_PROTECTED_RESOURCE_PATH);

		return new ModelAndView("home", model.asMap());
	}

	@GetMapping(SING_IN_VIA_GIP_HUB_PATH)
	public RedirectView singInViaGipHub(@CookieValue(SESSION_COOKIE) final String sessionId) {
		var state = createStateAndWriteToSession(sessionId);

		var authEndpointURI = URIBuilder.buildAuthorizationURI(state);
		var redirect = new RedirectView(authEndpointURI);
		return redirect;
	}

	@GetMapping("/callback")
	public ResponseEntity<?> callback(@RequestParam final String code, @RequestParam final String state, @CookieValue(SESSION_COOKIE) final String sessionId, HttpServletRequest request) {
		var sessionState = getState(sessionId);
		if (state.equals(sessionState)) {
			removeState(sessionId);
		} else {
			return ResponseEntity.badRequest().body("state does not correspond. expected state: '%s', received state: '%s'".formatted(sessionState, state));
		}

		try {
			var tokenStrategy = getTokenStrategy(sessionId);
			var rawJsonToken = AppHttpClient.sendTokenRequest(code);
			Token token = TokenParser.parse(rawJsonToken, tokenStrategy);
			switch (token) {
			case AccessToken accessToken:
				saveAccessToken(sessionId, accessToken);
				break;
			case RefreshAndAccessTokensPair refreshAndAccessTokens:
				saveAccessToken(sessionId, refreshAndAccessTokens.accessToken);
				saveRefreshToken(refreshAndAccessTokens.refreshToken);
				break;
			default:
				throw new RuntimeException("unknown token: " + token);
			}
			return ResponseEntity.ok("token received: %s".formatted(rawJsonToken));
		} catch (IOException | InterruptedException e) {
			var logger = LogFactory.getLog(getClass());
			var message = "failed to get the token: %s".formatted(e.getMessage());
			logger.info(message, e);
			logger.error(message, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping(FETCH_PROTECTED_RESOURCE_PATH)
	public ResponseEntity<?> fetchProtectedResource(@CookieValue(SESSION_COOKIE) final String sessionId) {
		var accessToken = getAccessToken(sessionId);

		if (accessToken == null) {
			return ResponseEntity.badRequest().body("access token is not found in session");
		}

		try {
			var resource = AppHttpClient.fetchProtectedResource(accessToken);
			var message = "successfully fetched: %s".formatted(resource);
			return ResponseEntity.ok().body(message);
		} catch (IOException | InterruptedException e) {
			var message = "failed! authorize: <a href=\"%s\">sign in via gip hub</a> message: %s".formatted(SING_IN_VIA_GIP_HUB_PATH, e.getMessage());
			return ResponseEntity.internalServerError().body(message);
		}
	}

	private Session getSession(final String sessionId) {
		if (sessionId == null) {
			throw new IllegalArgumentException("session not found");
		} else {
			return sessionRepository.findById(sessionId);
		}
	}

	private String getState(final String sessionId) {
		var session = getSession(sessionId);
		if (session == null) {
			return null;
		}
		var sessionState = (String) session.getAttribute(STATE);
		return sessionState;
	}

	private RefreshToken getRefreshToken() {
		return Database.getRefreshToken();
	}

	private void saveRefreshToken(final RefreshToken refreshToken) {
		Database.saveRefreshToken(refreshToken);
	}

	private AccessToken getAccessToken(final String sessionId) {
		var session = getSession(sessionId);
		if (session == null) {
			return null;
		}
		var accessToken = (AccessToken) session.getAttribute(TOKEN);
		return accessToken;
	}

	private void saveAccessToken(final String sessionId, final AccessToken accessToken) {
		var session = getSession(sessionId);
		session.setAttribute(TOKEN, accessToken);
		saveSession(session);
	}

	private Token getToken(final String sessionId) {
		var tokenStrategy = getTokenStrategy(sessionId);
		var token = switch (tokenStrategy) {
		case SINGLE_ACCESS_TOKEN -> getAccessToken(sessionId);
		case REFRESH_AND_ACCESS_PAIR -> new RefreshAndAccessTokensPair(getRefreshToken(), getAccessToken(sessionId));
		default -> throw new RuntimeException("unknown token strategy: " + tokenStrategy);
		};
		return token;
	}

	private void removeState(final String sessionId) {
		var session = getSession(sessionId);
		if (session == null) {
			return;
		}
		session.removeAttribute(STATE);
		saveSession(session);
	}

	private void saveSession(final Session session) {
		sessionRepository.save((MapSession) session);
	}

	/**
	 * @return String sessionId
	 */
	private String ensureSessionExists(@CookieValue(SESSION_COOKIE) final String sessionId, final HttpServletResponse response) {
		var session = getSession(sessionId);
		if (session != null) {
			return session.getId();
		}

		session = sessionRepository.createSession();
		setTokenStrategy(session, SINGLE_ACCESS_TOKEN);
		saveSession(session);

		var newSessionCookie = new Cookie(SESSION_COOKIE, session.getId());
		response.addCookie(newSessionCookie);

		return session.getId();
	}

	private TokenStrategy getTokenStrategy(final String sessionId) {
		var session = getSession(sessionId);
		return session.getAttribute(TOKEN_STRATEGY);
	}

	private void setTokenStrategy(final Session session, final TokenStrategy tokenStrategy) {
		session.setAttribute(TOKEN_STRATEGY, tokenStrategy);
	}

	private String createStateAndWriteToSession(final String sessionId) {
		var session = getSession(sessionId);

		var state = service.generateState();
		session.setAttribute(STATE, state);

		saveSession(session);
		return state;
	}

}
