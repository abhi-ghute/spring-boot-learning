package com.example.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringJwtExampleApplication {

	public static void main(String[] args) throws ClassNotFoundException {

		SpringApplication.run(SpringJwtExampleApplication.class, args);
		Class.forName("com.mysql.cj.jdbc.Driver");
//		System.out.println("Driver class found!");
	}

}
