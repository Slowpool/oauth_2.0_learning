package com.swetlokognatsk.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@RestController
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@RequestMapping("/")
	public String home() {
		var content = "<h1>Hello ClientApplication</h1>" +
			"<button type=\"button\" onclick=\"window.location.href='%s';\">" +
						"Sign in via GipHub" +
			"</button>";
		var authEndpointURI = URIBuilder.buildAuthorizationURI();
		content = String.format(content, authEndpointURI);
		return content;
	}

}
