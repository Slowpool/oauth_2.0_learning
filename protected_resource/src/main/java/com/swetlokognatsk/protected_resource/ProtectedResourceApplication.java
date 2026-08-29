package com.swetlokognatsk.protected_resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.swetlokognatsk.protected_resource.models.AccessTokenValue;
import com.swetlokognatsk.protected_resource.models.AccessToken;
import com.swetlokognatsk.protected_resource.models.AccessTokenBody;
import com.swetlokognatsk.protected_resource.services.AccessTokenVerifier;

import jakarta.servlet.http.HttpServletRequest;

import static com.swetlokognatsk.protected_resource.services.AuthHeaderHelper.*;

import java.util.Map;

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

	@RequestMapping("/resource/fetch")
	// https://stackoverflow.com/questions/60671020/how-to-get-spring-boot-to-map-query-parameters-separately-from-form-data
	// in prod only one way of getting accessToken must be implemented. whereas here may be collisions, though it works well for any request
	public String fetchProtectedResource(@RequestHeader(name = "Authorization", required = false) final String auth, @RequestBody(required = false) final MultiValueMap<String, String> formData, HttpServletRequest request) {
		String accessTokenValue;
		if (hasAuthBearerHeader(auth)) {
			accessTokenValue = cutAccessToken(auth);
		}
		else if (formData != null && hasFormUrlencodedToken(formData.getFirst("accessToken"))) {
			accessTokenValue = formData.getFirst("accessToken");
		} else if (hasQueryParamToken(request)) {
			accessTokenValue = request.getParameter("accessToken");
		} else {
			accessTokenValue = null;
		}

		String response;
		if (accessTokenValue != null) {
			var accessToken = new AccessTokenValue(accessTokenValue);
			try {
				verifyAccessToken(accessToken);
				response = "BAZINGA.PNG";
			} catch (AccessTokenNotFoundException e) {
				response = "400 accessToken is not found in database: %s".formatted(e.getMessage());
			}
		} else {
			response = "400 accessToken is not found";
		}
		return response;
	}

	private void verifyAccessToken(final AccessTokenValue accessTokenValue) throws AccessTokenNotFoundException {
		var tokenVerifier = ctx.getBean(AccessTokenVerifier.class);
		tokenVerifier.verifyAccessToken(accessTokenValue);
	}

}
