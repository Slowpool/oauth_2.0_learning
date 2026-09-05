package com.swetlokognatsk.authorization_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

// TODO create all base scenarios in oauth and write logs from each service in demonstration purposes
@SpringBootApplication(scanBasePackages = { "com.swetlokognatsk.authorization_server", "com.swetlokognatsk.oauth_db.models", "com.swetlokognatsk.oauth_db.daos" })
@EntityScan(basePackages = { "com.swetlokognatsk.authorization_server", "com.swetlokognatsk.oauth_db.models" })
public class AuthorizationServerApplication {

	public final static TokenStrategy TOKEN_STRATEGY = TokenStrategy.REFRESH_AND_ACCESS_PAIR;
	
	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServerApplication.class, args);
	}
}
