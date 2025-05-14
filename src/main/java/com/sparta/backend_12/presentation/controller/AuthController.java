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
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증/회원 API", description = "회원가입, 로그인 등 인증 기능을 제공합니다.")
@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "username, password, nickname 이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserSignupResponse.class),
                            examples = @ExampleObject(
                                    name = "회원가입 성공",
                                    summary = "회원가입 성공 예시",
                                    value = """
                                            {
                                              "username": "JIN HO",
                                              "nickname": "Mentos",
                                              "roles": [
                                                {
                                                  "role": "USER"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 가입된 사용자 또는 유효성 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "회원가입 실패 - 이미 가입된 사용자",
                                            summary = "이미 가입된 사용자",
                                            value = """
                                                    {
                                                      "error": {
                                                        "code": "USER_ALREADY_EXISTS",
                                                        "message": "이미 가입된 사용자입니다."
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "회원가입 실패 - 유효성 검증 실패",
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
            @Parameter(name = "nickname", description = "별명", example = "Mentos"),
    })
    public ResponseEntity<UserSignupResponse> signup(@RequestBody @Valid UserSignupRequest request) {
        log.info("signup.UserSignUpRequest: {}", request);
        UserSignupResponse response = authService.signup(request.toServiceDto());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "username ,password 가 필요합니다.")
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
    @Operation(
            summary = "관리자 권한 부여",
            description = "ADMIN 권한이 필요합니다.<br><br>username: admin<br>password: admin1234"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "관리자 권한 부여 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserEditResponse.class),
                            examples = @ExampleObject(
                                    name = "권한 부여 성공",
                                    summary = "관리자 권한 부여 성공 예시",
                                    value = """
                                {
                                  "username": "JIN HO",
                                  "nickname": "Mentos",
                                  "roles": [
                                    {
                                      "role": "Admin"
                                    }
                                  ]
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한이 부족한 경우(접근 제한)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponse.class),
                            examples = @ExampleObject(
                                    name = "권한 부족",
                                    summary = "접근 권한 없음",
                                    value = """
                                {
                                  "error": {
                                    "code": "ACCESS_DENIED",
                                    "message": "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."
                                  }
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    summary = "해당 ID의 사용자 없음",
                                    value = """
                                {
                                  "error": {
                                    "code": "USER_NOT_FOUND",
                                    "message": "사용자를 찾을 수 없습니다."
                                  }
                                }
                                """
                            )
                    )
            )
    })
    @Parameters({
            @Parameter(name = "userId", description = "권한을 부여할 사용자 ID", example = "1", required = true)
    })
    public ResponseEntity<UserEditResponse> grantAdminRole(@PathVariable("userId") Long userId) {
        log.info("admin/users/{userId}/roles: {}", userId);
        UserEditResponse response = authService.grantAdminRole(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
