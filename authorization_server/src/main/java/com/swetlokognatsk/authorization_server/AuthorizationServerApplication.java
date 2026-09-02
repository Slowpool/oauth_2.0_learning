package com.swetlokognatsk.authorization_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.swetlokognatsk.authorization_server.daos.ClientsDao;
import com.swetlokognatsk.authorization_server.exceptions.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.InvalidRedirectUriException;
import com.swetlokognatsk.authorization_server.models.Client;
import com.swetlokognatsk.authorization_server.models.RedirectUri;
import com.swetlokognatsk.authorization_server.ports.Database;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// TODO create all base scenarios in oauth and write logs from each service in demonstration purposes
@SpringBootApplication
@EntityScan(basePackages = { "com.swetlokognatsk.authorization_server", "com.swetlokognatsk.oauth_db.models" })
@RestController
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
	public ResponseEntity<?> authorize(@RequestParam(name = "client_id") final String clientId, @RequestParam(name = "redirect_uri") final String redirectUri) {
		var database = ctx.getBean(Database.class);
		try {
			var client = database.getClient(clientId);
			validateClient(client, redirectUri);//, clientSecret);
		}
		catch (ClientNotFoundException e) {
			return ResponseEntity.status(404).body("unknown client: %s".formatted(clientId));
		}
		catch (InvalidRedirectUriException e) {
			return ResponseEntity.unprocessableContent().body("incorrect redirectUri: %s".formatted(redirectUri));
		}

		return ResponseEntity.ok("/authorize, clientId: %s".formatted(clientId));
	}

	private void validateClient(final Client client, final String receivedRedirectUri) throws InvalidRedirectUriException {
		var clientRedirectUris = client.getRedirectUris()
			.stream()
			.map((RedirectUri redirectUri) -> redirectUri.uri)
			.toList();
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
