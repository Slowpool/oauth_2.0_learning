package com.swetlokognatsk.protected_resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public String home() {
		return "<h1>Hello ProtectedResourceApplication</h1>";
	}

	@RequestMapping("/resource/fetch")
	// https://stackoverflow.com/questions/60671020/how-to-get-spring-boot-to-map-query-parameters-separately-from-form-data
	// in prod only one way of getting accessToken must be implemented. whereas here may be collisions, though it works well for any request
	public ResponseEntity<String> fetchProtectedResource() {
		return ResponseEntity.ok("BAZINGA.PNG");
	}

}
