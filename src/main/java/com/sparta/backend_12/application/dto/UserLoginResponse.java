package com.sparta.backend_12.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 결과")
public record UserLoginResponse(
        @Schema(description = "JWT 토큰",
                example = "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ1c2VyIjogInRlc3QiLCAicm9sZSI6ICJhZG1pbiIsICJpYXQiOiAxNzQ3MTg1Mjc1fQ.Z0znuv6NR3At3lEvrVeQ_MFN7iBRXFRaoooig7INbGs")
        String token
) {
    public static UserLoginResponse from(String token) {
        return new UserLoginResponse(token);
    }
}
