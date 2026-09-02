package com.swetlokognatsk.authorization_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.swetlokognatsk.authorization_server.daos.ClientsDao;
import com.swetlokognatsk.authorization_server.ports.Database;

// TODO create all base scenarios in oauth and write logs from each service in demonstration purposes
@SpringBootApplication
@EntityScan(basePackages = { "com.swetlokognatsk.authorization_server", "com.swetlokognatsk.oauth_db.models" })
@RestController
public class AuthorizationServerApplication {

	private static ApplicationContext ctx;

	public static void main(String[] args) {
		ctx = SpringApplication.run(AuthorizationServerApplication.class, args);
	}

	@RequestMapping("/")
	public String home() {
		return "<h1>Hello AuthorizationServerApplication</h1>";
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
