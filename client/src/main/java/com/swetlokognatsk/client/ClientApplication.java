package com.swetlokognatsk.client;

import java.io.IOException;
import org.springframework.context.ApplicationContext;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.Thymeleaf;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@RestController
public class ClientApplication {

	private static final String FETCH_PROTECTED_RESOURCE_PATH = "/fetch-protected-resource";
	private static final String SING_IN_VIA_GIP_HUB_PATH = "/sing-in-via-gip-hub";

	private static ApplicationContext ctx;
	private static final String STATE = "state";
	private static final String SESSION_COOKIE = "CUSTOM_SESSION";
	private static final String TOKEN = "token";

	private final Service service;
	private final MapSessionRepository sessionRepository;

	public ClientApplication(final Service service, final MapSessionRepository sessionRepository) {
		this.service = service;
		this.sessionRepository = sessionRepository;
	}

	public static void main(String[] args) {
		ctx = SpringApplication.run(ClientApplication.class, args);
	}

	private String getState(final String sessionCookie) {
		var session = getSession(sessionCookie);
		if (session == null) {
			return null;
		}
		var sessionState = (String) session.getAttribute(STATE);
		return sessionState;
	}

	private Token getToken(final String sessionCookie) {
		var session = getSession(sessionCookie);
		if (session == null) {
			return null;
		}
		var token = (Token) session.getAttribute(TOKEN);
		return token;
	}

	private void flushStateFromSession(final String sessionCookie) {
		var session = getSession(sessionCookie);
		if (session == null) {
			return;
		}
		session.removeAttribute(STATE);
		sessionRepository.save((MapSession) session);
	}

	@RequestMapping("/")
	public String home(@CookieValue(SESSION_COOKIE) final String sessionCookie) {
		// TODO use thymeleaf
		var content = """
				<h1>Hello ClientApplication</h1>

				<h1>access token value: %s</h1>
				<h1>scope value: %s</h1>
				<h1>state: %s</h1>
				<h1></h1>

				<button type="button" onclick="window.location.href='%s';">
					<h1>Sign in via GipHub</h1>
					</button>
					.............
					<button type="button" onclick="window.open('%s');">
					<h1>Get protected resource</h1>
				</button>
				""";

		Token token = getToken(sessionCookie);
		String state = getState(sessionCookie);
		String accessToken = token == null ? "" : token.value();
		String scope = null;
		var getAuthEndpointURI = SING_IN_VIA_GIP_HUB_PATH;
		var askForProtectedResourceUri = FETCH_PROTECTED_RESOURCE_PATH;
		content = content.formatted(accessToken, scope, state, getAuthEndpointURI, askForProtectedResourceUri);

		return content;
	}

	@RequestMapping(SING_IN_VIA_GIP_HUB_PATH)
	public RedirectView singInViaGipHub(final HttpServletRequest request, final HttpServletResponse response) {
		var state = createStateAndWriteToSession(request, response);

		var authEndpointURI = URIBuilder.buildAuthorizationURI(state);
		var redirect = new RedirectView(authEndpointURI);
		return redirect;
	}

	private String createStateAndWriteToSession(final HttpServletRequest request, final HttpServletResponse response) {
		var session = createSession();

		var state = service.generateState();
		session.setAttribute(STATE, state);

		saveSession(session);
		var sessionCookie = new Cookie(SESSION_COOKIE, session.getId());
		response.addCookie(sessionCookie);
		return state;
	}

	@RequestMapping("/callback")
	public ResponseEntity<?> callback(@RequestParam final String code, @RequestParam final String state, @CookieValue(SESSION_COOKIE) final String sessionCookie, HttpServletRequest request) {
		var sessionState = getState(sessionCookie);
		if (state.equals(sessionState)) {
			flushStateFromSession(sessionCookie);
		} else {
			return ResponseEntity.badRequest().body("state does not correspond. expected state: '%s', received state: '%s'".formatted(sessionState, state));
		}

		try {
			var rawJsonToken = AppHttpClient.sendTokenRequest(code);
			var token = TokenParser.parse(rawJsonToken);
			saveTokenToSession(sessionCookie, token);
			return ResponseEntity.ok("token received: %s".formatted(rawJsonToken));
		} catch (IOException | InterruptedException e) {
			var logger = LogFactory.getLog(getClass());
			var message = "failed to get the token: %s".formatted(e.getMessage());
			logger.info(message, e);
			logger.error(message, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	private void saveTokenToSession(final String sessionId, final Token token) {
		var session = getSession(sessionId);
		session.setAttribute(TOKEN, token);
		saveSession(session);
	}

	private Session createSession() {
		var session = sessionRepository.createSession();
		return session;
	}

	private Session getSession(final String sessionId) {
		if (sessionId == null) {
			throw new IllegalArgumentException("session not found");
		} else {
			return sessionRepository.findById(sessionId);
		}
	}

	private void saveSession(final Session session) {
		sessionRepository.save((MapSession) session);
	}

	@RequestMapping(FETCH_PROTECTED_RESOURCE_PATH)
	public ResponseEntity<?> fetchProtectedResource(@CookieValue(SESSION_COOKIE) final String sessionCookie) {
		var token = getToken(sessionCookie);

		if (token == null) {
			return ResponseEntity.badRequest().body("access token is not found in session");
		}

		try {
			var resource = AppHttpClient.fetchProtectedResource(token);
			var message = "successfully fetched: %s".formatted(resource);
			return ResponseEntity.ok().body(message);
		} catch (IOException | InterruptedException e) {
			var message = "failed! %s".formatted(e.getMessage());
			return ResponseEntity.internalServerError().body(message);
		}
	}

}
