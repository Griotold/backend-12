package com.sparta.backend_12.application.dto;

public record UserSignup(
        String username,
        String password,
        String nickname
) {
}
