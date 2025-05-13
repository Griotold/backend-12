package com.sparta.backend_12.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.infra.repository.UserRepository;
import com.sparta.backend_12.presentation.dto.UserLoginRequest;
import com.sparta.backend_12.presentation.dto.UserSignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() throws Exception{
        // given
        UserSignupRequest request = new UserSignupRequest("JIN HO", "12341234", "Mentos");

        String requestBody = om.writeValueAsString(request);

        // when
        ResultActions resultActions = mvc.perform(post("/signup")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated());
        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("JIN HO"))
                .andExpect(jsonPath("$.nickname").value("Mentos"))
                .andExpect(jsonPath("$.roles[0].role").value("USER"));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 username")
    void signup_duplicateUsername_fail() throws Exception {
        // given
        String username = "JIN HO";
        String password = "12341234";
        String nickname = "Mentos";

        // 먼저 사용자 저장 (중복 상황 만들기)
        User existingUser = User.createAsUser(username, password, nickname);
        userRepository.save(existingUser);

        // 동일한 username으로 회원가입 시도
        UserSignupRequest request = new UserSignupRequest(username, password, nickname);
        String requestBody = om.writeValueAsString(request);

        // when & then
        mvc.perform(post("/signup")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())  // 409
                .andExpect(jsonPath("$.error.code").value("USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.error.message").value("이미 가입된 사용자입니다."));
    }

    @Test
    @DisplayName("로그인 성공 - JWT 토큰 반환")
    void login_Success_ReturnsJwtToken() throws Exception {
        // Given
        String username = "testUser";
        String password = "password123!";
        User user = User.createAsUser(username, passwordEncoder.encode(password), "TestNick");
        userRepository.save(user);

        UserLoginRequest request = new UserLoginRequest(username, password);
        String requestBody = om.writeValueAsString(request);

        // When
        ResultActions result = mvc.perform(post("/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_InvalidPassword_ThrowsException() throws Exception {
        // Given
        String username = "testUser";
        String password = "password123!";
        User user = User.createAsUser(username, passwordEncoder.encode(password), "TestNick");
        userRepository.save(user);

        UserLoginRequest request = new UserLoginRequest(username, "wrongPassword");
        String requestBody = om.writeValueAsString(request);

        // When & Then
        mvc.perform(post("/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.error.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_UserNotFound_ThrowsException() throws Exception {
        // Given
        UserLoginRequest request = new UserLoginRequest("nonExistingUser", "anyPassword");
        String requestBody = om.writeValueAsString(request);

        // When & Then
        mvc.perform(post("/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.error.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

}