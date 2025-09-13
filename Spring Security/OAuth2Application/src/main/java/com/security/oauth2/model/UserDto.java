package com.security.oauth2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserDto(

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        @JsonProperty("email")
        String email,

        @JsonProperty("role")
        RoleEnum role,

        @NotBlank(message = "Password is mandatory")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        @JsonProperty("password")
        String password,

        @JsonProperty("authorities")
        List<AuthorityEnum> authorities
) {
}