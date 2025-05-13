package com.sparta.backend_12.application.component;

import com.sparta.backend_12.application.exception.AuthException;
import com.sparta.backend_12.application.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PasswordValidatorTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordValidator passwordValidator;

    @DisplayName("validatePassword - 비밀번호 일치 시 예외 발생 없음")
    @Test
    void validatePassword_ValidPassword_NoException() {
        // given
        String rawPassword = "rawPassword123!";
        String encodedPassword = "encodedPassword";

        given(passwordEncoder.matches(rawPassword, encodedPassword))
                .willReturn(true);

        // when & then
        assertThatNoException()
                .isThrownBy(() ->
                        passwordValidator.validatePassword(rawPassword, encodedPassword)
                );
    }

    @DisplayName("validatePassword - 비밀번호 불일치 시 AuthException 발생")
    @Test
    void validatePassword_InvalidPassword_ThrowsAuthException() {
        // given
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";

        given(passwordEncoder.matches(rawPassword, encodedPassword))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() ->
                passwordValidator.validatePassword(rawPassword, encodedPassword)
        )
                .isInstanceOf(AuthException.class)
                .hasMessage(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }
}