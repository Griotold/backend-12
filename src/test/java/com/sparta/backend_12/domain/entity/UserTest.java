package com.sparta.backend_12.domain.entity;

import com.sparta.backend_12.domain.enums.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @DisplayName("create - 정적 팩토리 메서드 테스트")
    @Test
    void create_success() {
        // given
        String username = "JIN HO";
        String password = "12341234";
        String nickname = "Mentos";
        Role role = Role.USER;

        // when
        User user = User.create(username, password, nickname, role);

        // then
        assertThat(user.getId()).isNull(); // 아직 영속화 전이므로 null 이어야 함
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getRole()).isEqualTo(role);
    }

}