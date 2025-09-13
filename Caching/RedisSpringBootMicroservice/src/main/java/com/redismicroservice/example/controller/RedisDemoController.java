package com.redismicroservice.example.controller;

import com.redismicroservice.example.mdoel.User;
import com.redismicroservice.example.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisDemoController {

    @Autowired
    RedisService redisService;

    @PostMapping("/saveUser")
    public String saveUser(@RequestBody User userDTO){
        return redisService.saveUser(userDTO);
    }

    @GetMapping("/getUser/{user_id}")
    public User getUser(@PathVariable(name = "user_id")  String userId){
        return redisService.getUser(userId);
    }
}
