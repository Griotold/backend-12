package com.sparta.backend_12.presentation.dto;

import com.sparta.backend_12.application.dto.UserLogin;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank(message = "username 은 필수 입력값입니다.")
        String username,
        @NotBlank(message = "password 은 필수 입력값입니다.")
        String password
) {
    public UserLogin toServiceDto() {
        return new UserLogin(username, password);
    }
}
