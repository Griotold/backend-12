package com.sparta.backend_12.infra.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.backend_12.application.exception.ErrorCode;
import com.sparta.backend_12.application.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper om;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {
        ErrorCode errorCode = ErrorCode.INVALID_TOKEN;

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                new ExceptionResponse.Error(
                        errorCode.name(),
                        errorCode.getMessage()
                )
        );

        try {
            om.writeValue(response.getWriter(), exceptionResponse);
        } catch (IOException e) {
            log.error("CustomAuthenticationEntryPoint 응답 작성 실패: {}", e.getMessage());
        }
    }
}
