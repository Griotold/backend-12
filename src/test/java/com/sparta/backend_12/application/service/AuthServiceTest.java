package com.sparta.backend_12.application.service;

import com.sparta.backend_12.application.dto.UserLogin;
import com.sparta.backend_12.application.dto.UserLoginResponse;
import com.sparta.backend_12.application.dto.UserSignup;
import com.sparta.backend_12.application.dto.UserSignupResponse;
import com.sparta.backend_12.application.exception.AuthException;
import com.sparta.backend_12.application.exception.ErrorCode;
import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.domain.enums.Role;
import com.sparta.backend_12.infra.config.security.JwtTokenProvider;
import com.sparta.backend_12.infra.repository.UserRepository;
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
    @Autowired JwtTokenProvider jwtTokenProvider;

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

    }

    @Test
    @DisplayName("login 성공 - 유효한 자격증명으로 JWT 토큰 반환")
    void login_Success_ReturnsJwtToken() {
        // given
        User user = User.createAsUser("testUser", passwordEncoder.encode("password123!"), "TestNick");
        userRepository.save(user);
        UserLogin request = new UserLogin("testUser", "password123!");

        // when
        UserLoginResponse response = authService.login(request);

        // then
        assertThat(response.token()).isNotBlank();
        String username = jwtTokenProvider.extractUsername(response.token());
        assertThat(username).isEqualTo("testUser");
    }

    @Test
    @DisplayName("login 실패 - 존재하지 않는 사용자명")
    void login_UserNotFound_ThrowsException() {
        // given
        UserLogin request = new UserLogin("nonExistingUser", "anyPassword");

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    @DisplayName("login 실패 - 잘못된 비밀번호")
    void login_WrongPassword_ThrowsException() {
        // given
        User user = User.createAsUser("testUser", passwordEncoder.encode("password123!"), "TestNick");
        userRepository.save(user);
        UserLogin request = new UserLogin("testUser", "wrongPassword");

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }
}