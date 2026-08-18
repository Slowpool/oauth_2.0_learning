package com.swetlokognatsk.protected_resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@RestController
public class ProtectedResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProtectedResourceApplication.class, args);
	}

	@RequestMapping("/")
	public String home() {
		return "<h1>Hello ProtectedResourceApplication</h1>";
	}
}
