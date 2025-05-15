package com.sparta.backend_12.application.dto;

import com.sparta.backend_12.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
@Schema(description = "회원가입 결과")
public record UserSignupResponse(
        @Schema(description = "회원 이름", example ="JIN HO")
        String username,
        @Schema(description = "별명", example = "Mentos")
        String nickname,
        @Schema(description = "권한 목록", example = "[{\"role\": \"USER\"}]")
        List<RoleResponse> roles
) {
    public static UserSignupResponse from(final User user) {
        return new UserSignupResponse(
                user.getUsername(),
                user.getNickname(),
                List.of(new RoleResponse(user.getRole().name()))
        );
    }

    @Schema(description = "권한 정보")
    public record RoleResponse(
            @Schema(description = "권한 이름", example = "USER")
            String role
    ) {}
}
