package com.sparta.backend_12.application.dto;

public record UserLoginResponse(
        String token
) {
    public static UserLoginResponse from(String token) {
        return new UserLoginResponse(token);
    }
}
