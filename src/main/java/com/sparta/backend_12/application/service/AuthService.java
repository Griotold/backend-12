package com.sparta.backend_12.application.service;

import com.sparta.backend_12.application.component.UsernameValidator;
import com.sparta.backend_12.application.dto.UserSignup;
import com.sparta.backend_12.application.dto.UserSignupResponse;
import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsernameValidator validator;

    @Transactional
    public UserSignupResponse signup(UserSignup signup) {
        log.info("signup.UserSignup: {}", signup);

        validator.validateDuplicateUsername(signup.username());

        User user = User.createAsUser(
                signup.username(),
                passwordEncoder.encode(signup.password()),
                signup.nickname()
        );
        return UserSignupResponse.from(userRepository.save(user));
    }
}
