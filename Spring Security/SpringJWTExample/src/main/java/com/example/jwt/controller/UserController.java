package com.example.jwt.controller;

import com.example.jwt.entity.UserEntity;
import com.example.jwt.mapper.UserMapper;
import com.example.jwt.model.UserDto;
import com.example.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @PostMapping("/register")
    public String register(@RequestBody UserDto userDto){

        UserEntity userEntity = userMapper.toEntity(userDto);
        userRepository.saveAndFlush(userEntity);

        return "Success";
    }

}
