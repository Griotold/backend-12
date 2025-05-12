package com.sparta.backend_12.application.service;

import com.sparta.backend_12.application.dto.UserSignup;
import com.sparta.backend_12.application.dto.UserSignupResponse;
import com.sparta.backend_12.application.exception.AuthException;
import com.sparta.backend_12.application.exception.ErrorCode;
import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.domain.enums.Role;
import com.sparta.backend_12.infra.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AuthServiceTest {

    @Autowired AuthService authService;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @DisplayName("회원가입 성공 - 정상적인 요청 시 사용자 생성")
    @Test
    void signup_success() {
        // given
        UserSignup signup = new UserSignup("yuju", "1234", "Mentos");

        // when
        UserSignupResponse response = authService.signup(signup);

        // then
        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo(signup.username());
        assertThat(response.nickname()).isEqualTo(signup.nickname());
        assertThat(response.roles()).hasSize(1);
        assertThat(response.roles().getFirst().role()).isEqualTo(Role.USER.name());

        // 비밀번호 암호화 검증
        User savedUser = userRepository.findByUsername(signup.username()).orElseThrow();
        assertThat(savedUser.getPassword()).isNotEqualTo(signup.password());
        assertThat(passwordEncoder.matches(signup.password(), savedUser.getPassword())).isTrue();
    }

    @DisplayName("회원가입 실패 - 중복된 username 존재 시 예외 발생")
    @Test
    void signup_duplicateUsername_throwsException() {
        // given
        String duplicateUsername = "existingUser";
        User existingUser = User.createAsUser(duplicateUsername, "password", "기존닉네임");
        userRepository.save(existingUser);

        UserSignup newRequest = new UserSignup(duplicateUsername, "newPassword1!", "새닉네임");

        // when & then
        assertThatThrownBy(() -> authService.signup(newRequest))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorCode.USER_ALREADY_EXISTS.getMessage());

        // 중복 데이터 개수 확인
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(1);
    }
}