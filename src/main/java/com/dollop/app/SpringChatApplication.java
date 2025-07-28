package com.dollop.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication

//@CrossOrigin(origins = "http://localhost:4200/", allowCredentials = "true")
public class SpringChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringChatApplication.class, args);
	}

}
