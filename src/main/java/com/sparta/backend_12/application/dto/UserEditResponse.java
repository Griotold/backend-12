package com.sparta.backend_12.application.dto;

import com.sparta.backend_12.domain.entity.User;

import java.util.List;

public record UserEditResponse(
        String username,
        String nickname,
        List<RoleResponse> roles
) {
    public static UserEditResponse from(final User user) {
        return new UserEditResponse(
                user.getUsername(),
                user.getNickname(),
                List.of(new RoleResponse(user.getRole().name()))
        );
    }

    public record RoleResponse(String role) {}
}
