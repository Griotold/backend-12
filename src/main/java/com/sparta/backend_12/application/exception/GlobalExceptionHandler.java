package com.sparta.backend_12.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final String ERROR_LOG = "[ERROR] %s %s";

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse> applicationException(final AuthException e) {
        log.error(String.format(ERROR_LOG, e.getHttpStatus(), e.getErrorCode().getMessage()));

        ErrorCode errorCode = e.getErrorCode();
        ExceptionResponse.Error error = new ExceptionResponse.Error(
                errorCode.name(),
                errorCode.getMessage()
        );

        return ResponseEntity.status(e.getHttpStatus())
                .body(new ExceptionResponse(error));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error(String.format(ERROR_LOG, e.getMessage(), e.getClass().getName()));
        ExceptionResponse.Error error = new ExceptionResponse.Error("NOT_FOUND", "지원하지 않는 경로입니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(error));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> httpReqMethodNotSupportException(final HttpRequestMethodNotSupportedException e){
        log.error(String.format(ERROR_LOG, e.getMessage(), Arrays.toString(e.getSupportedMethods())));
        ExceptionResponse.Error error = new ExceptionResponse.Error("METHOD_NOT_ALLOWED", "지원하지 않는 요청 방법입니다.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ExceptionResponse(error));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> missingServletRequestParameter(final MissingServletRequestParameterException e) {
        log.error(String.format(ERROR_LOG, e.getParameterName(), e.getMessage()));
        ExceptionResponse.Error error = new ExceptionResponse.Error("MISSING_PARAMETER", "필요한 파라미터가 입력되지 않았습니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentNotValidException(final MethodArgumentNotValidException e){
        log.error(String.format(ERROR_LOG, e.getParameter(), e.getStatusCode()));
        String defaultMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ExceptionResponse.Error error = new ExceptionResponse.Error("VALIDATION_ERROR", defaultMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(error));
    }
}
