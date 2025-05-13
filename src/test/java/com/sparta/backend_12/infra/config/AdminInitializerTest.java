package com.sparta.backend_12.infra.config;


import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.domain.enums.Role;
import com.sparta.backend_12.infra.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AdminInitializerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminInitializer adminInitializer;

    @Autowired
    private EntityManager em;

    @DisplayName("run - 서버 실행과 동시에 admin 계정이 만들어진다.")
    @Test
    void run_whenAdminUserExists() throws Exception {
        // when
        User admin = userRepository.findByUsername("admin").orElseThrow();

        // then: admin 계정 확인
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
    }

    @DisplayName("run - user 를 모두 지운 후 호출")
    @Test
    void run_success() throws Exception {
        // given: 초기 데이터 삭제
        userRepository.deleteAll();

        // when: AdminInitializer 수동 실행
        adminInitializer.run();

        // then: 관리자 계정 존재 확인
        User admin = userRepository.findByUsername("admin").orElseThrow();
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
    }
}