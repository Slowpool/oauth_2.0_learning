package com.swetlokognatsk.authorization_server;

import static org.springframework.http.HttpStatus.*;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.swetlokognatsk.authorization_server.daos.ClientsDao;
import com.swetlokognatsk.authorization_server.exceptions.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.InvalidRedirectUriException;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;
import com.swetlokognatsk.authorization_server.models.Client;
import com.swetlokognatsk.authorization_server.models.RedirectUri;
import com.swetlokognatsk.authorization_server.ports.Database;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// TODO create all base scenarios in oauth and write logs from each service in demonstration purposes
@SpringBootApplication
@EntityScan(basePackages = { "com.swetlokognatsk.authorization_server", "com.swetlokognatsk.oauth_db.models" })
@Controller
public class AuthorizationServerApplication {

	private static final String HOME = "/";
	private static final String AUTHORIZATION_ENDPOINT = "/authorize";

	private static ApplicationContext ctx;

	public static void main(String[] args) {
		ctx = SpringApplication.run(AuthorizationServerApplication.class, args);
	}

	@RequestMapping(HOME)
	public String home() {
		return "<h1>Hello AuthorizationServerApplication</h1>";
	}

	@GetMapping(AUTHORIZATION_ENDPOINT)
	public ModelAndView authorize(final HttpServletResponse response, @RequestParam(name = "client_id") final String clientId, @RequestParam(name = "redirect_uri") final String redirectUri, final Model model) {
		var database = ctx.getBean(Database.class);

		String view;

		try {
			var client = database.getClient(clientId);
			validateClient(client, redirectUri);//, clientSecret);
			var requestId = saveAuthorizationRequest(database, clientId, redirectUri);
			//clientId, requestId));
			model.addAttribute("requestId", requestId);
			model.addAttribute("clientId", clientId);
			model.addAttribute("redirectUri", redirectUri);
			view = "approve";
		} catch (ClientNotFoundException e) {
			model.addAttribute("error", "unknown client: %s".formatted(clientId));
			response.setStatus(NOT_FOUND.value());
			view = "error";
		} catch (InvalidRedirectUriException e) {
			model.addAttribute("error", "incorrect redirectUri: %s".formatted(redirectUri));
			response.setStatus(UNPROCESSABLE_CONTENT.value());
			view = "error";
		}
		return new ModelAndView(view, model.asMap());
	}

	/**
	 * @return String requestId
	 */
	private String saveAuthorizationRequest(final Database database, final String clientId, final String redirectUri) {
		String requestId = UUID.randomUUID().toString();
		var authorizationRequest = new AuthorizationRequest(requestId, clientId, redirectUri);
		database.saveAuthorizationRequest(authorizationRequest);
		return requestId;
	}

	private void validateClient(final Client client, final String receivedRedirectUri) throws InvalidRedirectUriException {
		var clientRedirectUris = client.getRedirectUris().stream().map((RedirectUri redirectUri) -> redirectUri.uri).toList();
		if (!clientRedirectUris.contains(receivedRedirectUri)) {
			throw new InvalidRedirectUriException();
		}
	}

	@RequestMapping("/clients-test")
	public String clientsTest() {
		var clientsDao = ctx.getBean(ClientsDao.class);
		var clients = clientsDao.getClients();
		var firstClient = clients.getFirst();
		var redirectUris = firstClient.getRedirectUris();
		int numberOfRedirectUris = redirectUris.size();
		var firstRedirectUri = redirectUris.getFirst();
		return "done";
	}

	@RequestMapping("/clients-test2")
	public String clientsTest2() {
		var database = ctx.getBean(Database.class);
		try {
			var client = database.getClient("client-1");
			return "done";
		} catch (ClientNotFoundException e) {
			return "fail: " + e.getMessage();
		}
	}
}
