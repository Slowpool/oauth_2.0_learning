package com.swetlokognatsk.protected_resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import static com.swetlokognatsk.protected_resource.AuthHeaderHelper.*;
import static com.swetlokognatsk.protected_resource.AccessTokenVerifier.*;

// TODO is it possible to configure vs code to run all 4 debuggers
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@RestController
public class ProtectedResourceApplication {

	private static final String HOME = "/";

	public static void main(String[] args) {
		SpringApplication.run(ProtectedResourceApplication.class, args);
	}

	@GetMapping(HOME)
	public String home(@RequestHeader(name = "Authorization", required = false) final String auth) {
		return "<h1>Hello ProtectedResourceApplication</h1>";
	}

	@GetMapping("/resource/fetch")
	// TODO why not RequestBody? what if RequestBody param has the same name as RequestParam and both of them are in request?
	public String fetchProtectedResource(@RequestHeader(name = "Authorization", required = false) final String auth, @RequestBody(required = false) final AccessTokenBody accessTokenBody, @RequestParam(required = false) final String accessTokenParam) {
		String accessTokenValue;
		if (hasAuthBearerHeader(auth)) {
			accessTokenValue = cutAccessToken(auth);
		}
		// TODO does it work? or it has null as accessToken value?
		else if (hasFormUrlencodedToken(accessTokenBody)) {
			accessTokenValue = accessTokenBody.accessToken();
		}
		else if (hasQueryParamToken(accessTokenParam)) {
			accessTokenValue = accessTokenParam;
		}
		else {
			accessTokenValue = null;
		}

		String response;
		if (accessTokenValue != null) {
			var accessToken = new AccessToken(accessTokenValue);
			verifyAccessToken(accessToken);
			response = "BAZINGA.PNG";
		}
		else {
			response = "accessToken is not found";
		}
		return response;
	}

}
