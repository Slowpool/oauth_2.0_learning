package com.swetlokognatsk.protected_resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProtectedResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProtectedResourceApplication.class, args);
	}

	@RequestMapping("/")
	public String home() {
		return "Hello ProtectedResourceApplication";
	}
}
