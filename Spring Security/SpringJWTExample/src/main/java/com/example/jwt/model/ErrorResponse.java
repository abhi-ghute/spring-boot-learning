package com.example.jwt.model;

public record ErrorResponse(
        String message,
        String exception,
        int status,
        String timestamp,
        String path
) {}
