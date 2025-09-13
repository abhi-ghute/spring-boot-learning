package com.redismicroservice.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redismicroservice.example.mdoel.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    @Autowired
    DBService dbService;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    Logger logger = LoggerFactory.getLogger(RedisService.class);

    public String saveUser(User user){
        String result = dbService.saveUser(user);
        System.out.println("hello");

        logger.info("User stored in cache");
        redisTemplate.opsForValue().set(user.getUserId(),user, Duration.ofMinutes(10));
        return result;
    }

    public User getUser(String userID){
        User result = (User) redisTemplate.opsForValue().get(userID);

        if(result == null){
            return  dbService.getUser(userID);
        }
        logger.info("User returned from cache");
        return result;
    }
}
