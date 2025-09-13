package com.security.oauth2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record JWTResponse(
        @JsonProperty("email")
        String email,

        @JsonProperty("roles")
        List<String> roles,

        @JsonProperty("jwt")
        String jwt
) {
}
