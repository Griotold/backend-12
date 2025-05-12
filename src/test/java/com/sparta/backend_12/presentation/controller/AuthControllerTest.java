package com.sparta.backend_12.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.infra.UserRepository;
import com.sparta.backend_12.presentation.dto.UserSignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
}