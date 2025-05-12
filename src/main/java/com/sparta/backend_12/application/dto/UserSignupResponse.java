package com.sparta.backend_12.application.dto;

import com.sparta.backend_12.domain.entity.User;
import java.util.List;

public record UserSignupResponse(
        String username,
        String nickname,
        List<RoleResponse> roles
) {
    public static UserSignupResponse from(final User user) {
        return new UserSignupResponse(
                user.getUsername(),
                user.getNickname(),
                List.of(new RoleResponse(user.getRole().name()))
        );
    }

    public record RoleResponse(String role) {}
}
