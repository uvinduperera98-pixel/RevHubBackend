package com.revHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RevHub {

	public static void main(String[] args) {
		SpringApplication.run(RevHub.class, args);
	}

}
