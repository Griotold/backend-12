package com.sparta.backend_12.presentation.dto;

import com.sparta.backend_12.application.dto.UserLogin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
@Schema(description = "로그인 입력 정보")
public record UserLoginRequest(
        @Schema(description = "회원 이름", example = "JIN HO")
        @NotBlank(message = "username 은 필수 입력값입니다.")
        String username,
        @Schema(description = "비밀번호", example = "12341234")
        @NotBlank(message = "password 은 필수 입력값입니다.")
        String password
) {
    public UserLogin toServiceDto() {
        return new UserLogin(username, password);
    }
}
