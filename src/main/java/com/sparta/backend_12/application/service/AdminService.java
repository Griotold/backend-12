package com.sparta.backend_12.application.service;

import com.sparta.backend_12.application.dto.UserEditResponse;
import com.sparta.backend_12.application.exception.AuthException;
import com.sparta.backend_12.application.exception.ErrorCode;
import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    @Transactional
    public UserEditResponse grantAdminRole(Long userId) {
        log.info("grantAdminRole.userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        user.updateAdminRole();
        return UserEditResponse.from(user);
    }
}
