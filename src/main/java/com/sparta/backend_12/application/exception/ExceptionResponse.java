package com.sparta.backend_12.application.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 응답")
public record ExceptionResponse(
        @Schema(description = "에러 정보")
        Error error
) {
    @Schema(description = "에러 상세 정보")
    public record Error(
            @Schema(description = "에러 코드", example = "INVALID_CREDENTIALS")
            String code,
            @Schema(description = "에러 메시지", example = "아이디 또는 비밀번호가 올바르지 않습니다.")
            String message) {}
}

