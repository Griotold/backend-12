package com.sparta.backend_12.domain.entity;

import com.sparta.backend_12.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "a_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static User create(String username, String password, String nickname, Role role) {
        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .role(role)
                .build();
    }

    public static User createAsUser(String username, String password, String nickname) {
        return create(username, password, nickname, Role.USER);
    }
}
