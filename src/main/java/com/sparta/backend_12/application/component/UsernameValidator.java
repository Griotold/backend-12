package com.sparta.backend_12.application.component;

import com.sparta.backend_12.application.exception.AuthException;
import com.sparta.backend_12.application.exception.ErrorCode;
import com.sparta.backend_12.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UsernameValidator {

    private final UserRepository userRepository;

    public void validateDuplicateUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new AuthException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }
}
