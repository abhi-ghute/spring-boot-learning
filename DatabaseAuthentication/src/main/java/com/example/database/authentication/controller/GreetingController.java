package com.example.database.authentication.controller;

import com.example.database.authentication.entity.User;
import com.example.database.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class GreetingController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/hello")
    public String sayHello(){
        return "hello";
    }

    @GetMapping("/hi")
    public String sayHi(){
        return "Hi";
    }

    @PostMapping("/register")
    public String register(@RequestBody User user){

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "Success";
    }
}
