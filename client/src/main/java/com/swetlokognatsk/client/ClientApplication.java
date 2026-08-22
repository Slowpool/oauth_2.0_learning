package com.swetlokognatsk.client;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;
import org.thymeleaf.Thymeleaf;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@RestController
public class ClientApplication {

	private static ApplicationContext ctx;
	private static final String STATE = "state";
	private static final String SESSION_COOKIE = "CUSTOM_SESSION";

	public static void main(String[] args) {
		ctx = SpringApplication.run(ClientApplication.class, args);
	}

	@RequestMapping("/")
	public String home() {
		// TODO use thymeleaf
		var content = "<h1>Hello ClientApplication</h1>" + "<h1>access token value: %s</h1>" + "<h1>scope value: %s</h1>" + "<h1></h1>" + "<h1></h1>" +

		// "<button type=\"button\" onclick=\"window.open('%s', '_blank');\">" +
				"<button type=\"button\" onclick=\"window.location.href='%s';\">" + "<h1>Sign in via GipHub</h1>" + "</button>" +

				"............." +

				"<button type=\"button\" onclick=\"window.open('%s');\">" + "<h1>Get protected resource</h1>" + "</button>";
		var getAuthEndpointURI = "/sing-in-via-gip-hub";
		String accessToken = null;
		String scope = null;
		var protectedResourceUri = URIBuilder.buildProtectedResourceURI();
		content = content.formatted(accessToken, scope, getAuthEndpointURI, protectedResourceUri);

		return content;
	}

	@RequestMapping("/sing-in-via-gip-hub")
	public RedirectView singInViaGipHub(final HttpServletRequest request, final HttpServletResponse response) {
		var state = createStateAndWriteToSession(request, response);

		var authEndpointURI = URIBuilder.buildAuthorizationURI(state);
		var redirect = new RedirectView(authEndpointURI);
		return redirect;
	}

	private String createStateAndWriteToSession(final HttpServletRequest request, final HttpServletResponse response) {
		var session = createSession(request);

		var state = generateState();
		session.setAttribute(STATE, state);

		saveSession(session);
		var sessionCookie = new Cookie(SESSION_COOKIE, session.getId());
		response.addCookie(sessionCookie);
		return state;
	}

	private static String generateState() {
		return UUID.randomUUID().toString();
	}

	@RequestMapping("/callback")
	public ResponseEntity<?> callback(@RequestParam final String code, @RequestParam final String state, @CookieValue(SESSION_COOKIE) final String sessionCookie, HttpServletRequest request) {
		var session = getSession(sessionCookie);
		var sessionState = session.getAttribute(STATE);
		if (!state.equals(sessionState)) {
			// TODO what's then? invalidate this state from session asking the user for repeating the whole process again?
			return ResponseEntity.badRequest().body("state does not correspond. expected state: '%s', received state: '%s'".formatted(sessionState, state));
		}

		try {
			var rawJsonToken = AppHttpClient.sendTokenRequest(code);
			var token = TokenParser.parse(rawJsonToken);
			// TODO save token to db. (use lightweight redis because that's learning app)
			return ResponseEntity.ok("token received: %s".formatted(rawJsonToken));
		} catch (IOException | InterruptedException e) {
			var logger = LogFactory.getLog(getClass());
			var message = "failed to get the token: %s".formatted(e.getMessage());
			logger.info(message, e);
			logger.error(message, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	private Session createSession(final HttpServletRequest request) {
		var sessionRepository = getSessionRepository();
		var session = sessionRepository.createSession();
		return session;
	}

	private Session getSession(final String sessionId) {
		if (sessionId == null) {
			throw new IllegalArgumentException("session not found");
		} else {
			var sessionRepository = getSessionRepository();
			return sessionRepository.findById(sessionId);
		}
	}

	private void saveSession(final Session session) {
		var sessionRepository = getSessionRepository();
		sessionRepository.save((MapSession)session);
	}

	private SessionRepository<MapSession> getSessionRepository() {
		return (SessionRepository<MapSession>) ctx.getBean(MapSessionRepository.class);
	}

}
