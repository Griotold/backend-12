package com.sparta.backend_12.presentation.dto;

import com.sparta.backend_12.application.dto.UserSignup;
import jakarta.validation.constraints.NotBlank;

public record UserSignupRequest(
        @NotBlank(message = "username 은 필수 입력값입니다.")
        String username,
        @NotBlank(message = "password 는 필수 입력값입니다.")
        String password,
        @NotBlank(message = "nickname 은 필수 입력값입니다.")
        String nickname
) {

    public UserSignup toServiceDto() {
        return new UserSignup(username, password, nickname);
    }
}
