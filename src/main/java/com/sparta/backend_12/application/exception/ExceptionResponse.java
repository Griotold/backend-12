package com.sparta.backend_12.application.exception;

public record ExceptionResponse(Error error) {
    public record Error(String code, String message) {}
}

