package com.security.oauth2.model;

public record ErrorResponse(
        String message,
        String exception,
        int status,
        String timestamp,
        String path
) {}
