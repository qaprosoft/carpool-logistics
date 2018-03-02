package com.asemenkov.carpool.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author asemenkov
 * @since Feb 16, 2018
 */
@SpringBootApplication(scanBasePackages = "com.asemenkov.carpool.logistics")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
