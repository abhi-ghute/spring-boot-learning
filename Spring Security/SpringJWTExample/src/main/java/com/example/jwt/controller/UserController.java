package com.example.jwt.controller;

import com.example.jwt.entity.UserEntity;
import com.example.jwt.mapper.UserMapper;
import com.example.jwt.model.UserDto;
import com.example.jwt.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @PostMapping("/register")
    public String register(@RequestBody @Valid UserDto userDto){

        UserEntity userEntity = userMapper.toEntity(userDto);
        userRepository.saveAndFlush(userEntity);

        return "Success";
    }

    @GetMapping("/profile")
    public UserDto profile(String email){

        UserEntity userEntity = userRepository.findByEmail(email);

        return userMapper.toDTO(userEntity);
    }
}
