package com.example.database.authentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class GreetingController {

    @GetMapping("/hello")
    public String sayHello(){
        return "hello";
    }

    @GetMapping("/hi")
    public String sayHi(){
        return "Hi";
    }
}
