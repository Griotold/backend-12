package com.sparta.backend_12.infra.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.backend_12.application.exception.ErrorCode;
import com.sparta.backend_12.application.exception.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper om;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.warn("사용자: {} 가 권한이 없는 리소스: {} 에 접근을 시도했습니다.",
                    auth.getName(), request.getRequestURI());
        }

        // 에러 코드 설정
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        // 응답 설정
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 응답 데이터 생성
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                new ExceptionResponse.Error(
                        errorCode.name(),
                        errorCode.getMessage()
                )
        );

        try {
            // JSON 형식으로 응답 작성
            om.writeValue(response.getWriter(), exceptionResponse);
        } catch (IOException e) {
            log.error("CustomAccessDeniedHandler 응답 작성 실패: {}", e.getMessage());
        }
    }
}

