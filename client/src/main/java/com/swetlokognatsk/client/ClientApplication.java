package com.swetlokognatsk.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.Thymeleaf;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@RestController
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@RequestMapping("/")
	public String home() {
		// TODO use thymeleaf
		var content = "<h1>Hello ClientApplication</h1>" +
			"<h1>access token value: %s</h1>" + 
			"<h1>scope value: %s</h1>" + 
			"<h1></h1>" + 
			"<h1></h1>" + 
			
			// "<button type=\"button\" onclick=\"window.open('%s', '_blank');\">" +
			"<button type=\"button\" onclick=\"window.location.href='%s';\">" +
				"<h1>Sign in via GipHub</h1>" +
			"</button>" +

			"............." +

			"<button type=\"button\" onclick=\"window.open('%s');\">" +
				"<h1>Get protected resource</h1>" +
			"</button>";
		var getAuthEndpointURI = "/sing-in-via-gip-hub";
		String accessToken = null;
		String scope = null;
		var protectedResourceUri = URIBuilder.buildProtectedResourceURI();
		content = content.formatted(accessToken, scope, getAuthEndpointURI, protectedResourceUri);
		return content;
	}

	@RequestMapping("/sing-in-via-gip-hub")
	public RedirectView singInViaGipHub() {
		var authEndpointURI = URIBuilder.buildAuthorizationURI();
		return new RedirectView(authEndpointURI);
	}
}
