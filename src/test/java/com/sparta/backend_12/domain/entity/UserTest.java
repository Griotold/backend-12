package com.sparta.backend_12.domain.entity;

import com.sparta.backend_12.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @DisplayName("create - 정적 팩토리 메서드 테스트 (Role USER, ADMIN)")
    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"USER", "ADMIN"})
    void create_success(Role role) {
        // given
        String username = "JIN HO";
        String password = "12341234";
        String nickname = "Mentos";

        // when
        User user = User.create(username, password, nickname, role);

        // then
        assertThat(user.getId()).isNull(); // 아직 영속화 전이므로 null이어야 함
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getRole()).isEqualTo(role);
    }

    @DisplayName("createAsUser - USER 역할로 생성 테스트")
    @Test
    void createAsUser_success() {
        // given
        String username = "JIN HO";
        String password = "12341234";
        String nickname = "Mentos";

        // when
        User user = User.createAsUser(username, password, nickname);

        // then
        assertThat(user.getId()).isNull(); // 아직 영속화 전이므로 null이어야 함
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @DisplayName("updateAdminRole - 역할을 ADMIN 으로 변경")
    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"USER", "ADMIN"})
    void updateAdminRole(Role initialRole) {
        // given: 초기 역할 설정
        User user = User.create(
                "testUser",
                "password123!",
                "테스트유저",
                initialRole
        );

        // when
        user.updateAdminRole();

        // then: 어떤 역할로 시작하든 ADMIN으로 변경
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

}