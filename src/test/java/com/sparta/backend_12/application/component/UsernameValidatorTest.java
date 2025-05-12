package com.sparta.backend_12.application.component;

import com.sparta.backend_12.application.exception.AuthException;
import com.sparta.backend_12.application.exception.ErrorCode;
import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.infra.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UsernameValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UsernameValidator usernameValidator;

    @DisplayName("validate - 중복된 username 이 존재하면 AuthException 발생")
    @Test
    void validateDuplicateUsername_whenDuplicateUsername_throwsException() {
        // given
        String username = "existingUser";
        User user = User.createAsUser(username, "password", "nickname");
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> usernameValidator.validateDuplicateUsername(username))
                .isInstanceOf(AuthException.class)
                .hasMessage(ErrorCode.USER_ALREADY_EXISTS.getMessage());
    }

    @DisplayName("validate - 중복된 username 이 없으면 예외 발생하지 않음")
    @Test
    void validateDuplicateUsername_uniqueUsername_noException() {
        // given
        String username = "newUser";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when & then
        assertThatNoException().isThrownBy(() -> usernameValidator.validateDuplicateUsername(username));
    }
}