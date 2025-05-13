package com.sparta.backend_12.infra.config;

import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.domain.enums.Role;
import com.sparta.backend_12.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("관리자 계정이 이미 존재합니다.");
            return;
        }

        User adminUser = User.create(
                "admin",
                passwordEncoder.encode("admin1234"),
                "administrator",
                Role.ADMIN
        );

        userRepository.save(adminUser);
        log.info("관리자 계정이 성공적으로 생성되었습니다.");
    }
}
