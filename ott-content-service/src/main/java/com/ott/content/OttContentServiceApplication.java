package com.ott.content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OttContentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OttContentServiceApplication.class, args);
	}

}
