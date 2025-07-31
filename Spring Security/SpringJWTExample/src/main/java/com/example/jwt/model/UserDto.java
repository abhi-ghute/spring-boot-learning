package com.example.jwt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UserDto(

    @JsonProperty("email")
    String email,

    @JsonProperty("role")
    RoleEnum role,

    @JsonProperty("password")
    String password,

    @JsonProperty("authorities")
    List<AuthorityEnum> authorities
){}