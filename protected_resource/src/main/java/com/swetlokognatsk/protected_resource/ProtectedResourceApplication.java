package com.swetlokognatsk.protected_resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.swetlokognatsk.protected_resource.models.AccessTokenValue;
import com.swetlokognatsk.protected_resource.models.AccessTokenBody;
import com.swetlokognatsk.protected_resource.services.AccessTokenVerifier;
import static com.swetlokognatsk.protected_resource.services.AuthHeaderHelper.*;

// TODO is it possible to configure vs code to run all 4 debuggers
@SpringBootApplication
@RestController
public class ProtectedResourceApplication {

	private static final String HOME = "/";
	private static ApplicationContext ctx;

	public static void main(String[] args) {
		ctx = SpringApplication.run(ProtectedResourceApplication.class, args);
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
		} else if (hasQueryParamToken(accessTokenParam)) {
			accessTokenValue = accessTokenParam;
		} else {
			accessTokenValue = null;
		}

		String response;
		if (accessTokenValue != null) {
			var accessToken = new AccessTokenValue(accessTokenValue);
			try {
				verifyAccessToken(accessToken);
			}
			catch (AccessTokenNotFoundException e) {
				response = "accessToken is not found: %s".formatted(e.getMessage());
			}
			response = "BAZINGA.PNG";
		} else {
			response = "accessToken is not found";
		}
		return response;
	}

	private void verifyAccessToken(final AccessTokenValue accessTokenValue) throws AccessTokenNotFoundException {
		var tokenVerifier = ctx.getBean(AccessTokenVerifier.class);
		tokenVerifier.verifyAccessToken(accessTokenValue);
	}

}
