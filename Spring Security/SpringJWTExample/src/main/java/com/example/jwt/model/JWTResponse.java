package com.example.jwt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JWTResponse(
        @JsonProperty("email")
        String email,

        @JsonProperty("jwt")
        String jwt
) {
}
