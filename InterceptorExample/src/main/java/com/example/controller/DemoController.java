package com.example.controller;

import com.example.dto.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    //in this call all the prehandle, posthandle and afterCompletion methods will gets executed
    @GetMapping("/api/user")
    public User getUser(@RequestHeader(name = "Authorization") String authorization){
        return new User("Ram",25,"Latur");
    }

    //in this call all the prehandle and afterCompletion methods executed
    //posthandle will not be executed because when exception thrown by from controller
    //it will skip posthandle method
    @GetMapping("/api/user/exception")
    public void getUserWithException(@RequestHeader(name = "Authorization") String authorization){
        throw new RuntimeException("Unexpected error in the API");
    }
}

