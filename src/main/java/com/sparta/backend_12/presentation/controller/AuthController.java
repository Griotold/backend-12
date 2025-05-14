package com.sparta.backend_12.presentation.controller;

import com.sparta.backend_12.application.dto.UserEditResponse;
import com.sparta.backend_12.application.dto.UserLoginResponse;
import com.sparta.backend_12.application.dto.UserSignupResponse;
import com.sparta.backend_12.application.exception.ExceptionResponse;
import com.sparta.backend_12.application.service.AuthService;
import com.sparta.backend_12.presentation.dto.UserLoginRequest;
import com.sparta.backend_12.presentation.dto.UserSignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증/회원 API", description = "회원가입, 로그인 등 인증 기능을 제공합니다.")
@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponse> signup(@RequestBody @Valid UserSignupRequest request) {
        log.info("signup.UserSignUpRequest: {}", request);
        UserSignupResponse response = authService.signup(request.toServiceDto());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인을 합니다.", description = "username 과 password 가 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserLoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "로그인 실패(잘못된 계정 정보) 또는 유효성 검증 실패(필수값 누락 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "로그인 실패",
                                            summary = "잘못된 계정 정보",
                                            value = """
                {
                  "error": {
                    "code": "INVALID_CREDENTIALS",
                    "message": "아이디 또는 비밀번호가 올바르지 않습니다."
                  }
                }
                """
                                    ),
                                    @ExampleObject(
                                            name = "유효성 검증 실패",
                                            summary = "필수값 누락",
                                            value = """
                {
                  "error": {
                    "code": "VALIDATION_ERROR",
                    "message": "username 은 필수 입력값입니다."
                  }
                }
                """
                                    )
                            }
                    )
            )
    })
    @Parameters({
            @Parameter(name = "username", description = "회원 이름", example = "JIN HO"),
            @Parameter(name = "password", description = "비밀번호", example = "12341234"),
    })
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
        log.info("login.UserLoginRequest: {}", request);
        UserLoginResponse response = authService.login(request.toServiceDto());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/admin/users/{userId}/roles")
    public ResponseEntity<UserEditResponse> grantAdminRole(@PathVariable("userId") Long userId) {
        log.info("admin/users/{userId}/roles: {}", userId);
        UserEditResponse response = authService.grantAdminRole(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
