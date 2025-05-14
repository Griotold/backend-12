package com.sparta.backend_12.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.infra.repository.UserRepository;
import com.sparta.backend_12.presentation.dto.UserLoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AdminControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("grantAdminRole 성공 - USER 를 ADMIN 으로 승격")
    void grantAdminRole_success() throws Exception {
        // given
        User user = User.createAsUser("normalUser", passwordEncoder.encode("pw1234!"), "일반유저");
        userRepository.save(user);

        String adminToken = getJwtToken("admin", "admin1234");

        // when
        ResultActions result = mvc.perform(patch("/admin/users/" + user.getId() + "/roles")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.roles[0].role").value("ADMIN"));
    }

    @Test
    @DisplayName("grantAdminRole 실패 - 존재하지 않는 사용자 ID")
    void grantAdminRole_userNotFound() throws Exception {
        // given
        Long notExistId = 99999L;
        String adminToken = getJwtToken("admin", "admin1234");

        // when
        ResultActions result = mvc.perform(patch("/admin/users/" + notExistId +"/roles")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("사용자를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("grantAdminRole 실패 - 권한 부족 (USER 역할)")
    void grantAdminRole_accessDenied() throws Exception {
        // 일반 사용자 생성 및 로그인
        User user = User.createAsUser("normalUser", passwordEncoder.encode("pw1234!"), "일반유저");
        userRepository.save(user);
        String userToken = getJwtToken("normalUser", "pw1234!");

        // when
        ResultActions result = mvc.perform(patch("/admin/users/1/roles")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.error.message").value("관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."));
    }

    @Test
    @DisplayName("grantAdminRole 실패 - jwt 토큰 없이 요청")
    void grantAdminRole_unauthorized() throws Exception {
        // when
        ResultActions result = mvc.perform(patch("/admin/users/1/roles")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.error.message").value("유효하지 않은 인증 토큰입니다."));
    }

    // 헬퍼 메서드: JWT 토큰 추출
    private String getJwtToken(String username, String password) throws Exception {
        UserLoginRequest loginRequest = new UserLoginRequest(username, password);
        String response = mvc.perform(post("/login")
                        .content(om.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        return om.readTree(response).get("token").asText();
    }
}