package com.sparta.backend_12.application.dto;

import com.sparta.backend_12.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
@Schema(description = "관리자 권한 부여 결과")
public record UserEditResponse(
        @Schema(description = "회원 이름", example = "JIN HO")
        String username,
        @Schema(description = "별명", example = "Mentos")
        String nickname,
        @Schema(description = "권한 목록", example = "[{\"role\": \"ADMIN\"}]")
        List<RoleResponse> roles
) {
    public static UserEditResponse from(final User user) {
        return new UserEditResponse(
                user.getUsername(),
                user.getNickname(),
                List.of(new RoleResponse(user.getRole().name()))
        );
    }

    @Schema(description = "권한 정보")
    public record RoleResponse(
            @Schema(description = "권한 이름", example = "ADMIN")
            String role
    ) {}
}
