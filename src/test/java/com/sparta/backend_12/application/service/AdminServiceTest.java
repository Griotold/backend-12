package com.sparta.backend_12.application.service;

import com.sparta.backend_12.application.exception.AuthException;
import com.sparta.backend_12.application.exception.ErrorCode;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AdminServiceTest {

    @Autowired AdminService adminService;
    @Autowired UserRepository userRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("grantAdminRole 성공 - USER 가 ADMIN 으로 승격됨")
    void grantAdminRole_success() {
        // given
        User user = User.createAsUser("normalUser", "pw1234!", "일반유저");
        User savedUser = userRepository.save(user);

        // when
        var response = adminService.grantAdminRole(savedUser.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.roles().getFirst().role()).isEqualTo(Role.ADMIN.name());

        // 실제 DB에서도 ADMIN으로 변경됐는지 확인
        em.flush();
        em.clear();

        User updated = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(updated.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("grantAdminRole 실패 - 존재하지 않는 userId")
    void grantAdminRole_userNotFound_throwsException() {
        // given
        Long notExistId = 99999L;

        // when & then
        assertThatThrownBy(() -> adminService.grantAdminRole(notExistId))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}