package com.redismicroservice.example.service;

import com.redismicroservice.example.mdoel.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class DBService {

    Logger logger = LoggerFactory.getLogger(DBService.class);

    public String saveUser(User userDTO){
        logger.info("Stored in DB");
        return "stored in Database";
    }

    public User getUser(String userId){
        logger.info("User Returned from DB");
        System.out.println(1/0);
        return User.builder()
                .userId("U123")
                .name("Abhishek")
                .username("abhig7")
                .address("Pune")
                .gender("Male")
                .build();
    }
}
