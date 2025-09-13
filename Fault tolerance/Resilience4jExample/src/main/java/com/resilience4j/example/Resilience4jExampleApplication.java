package com.resilience4j.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class Resilience4jExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(Resilience4jExampleApplication.class, args);
	}

}
