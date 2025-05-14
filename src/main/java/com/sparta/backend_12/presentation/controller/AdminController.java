package com.sparta.backend_12.presentation.controller;

import com.sparta.backend_12.application.dto.UserEditResponse;
import com.sparta.backend_12.application.exception.ExceptionResponse;
import com.sparta.backend_12.application.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 API", description = "회원가입, 로그인 등 인증 기능을 제공합니다.")
@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

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
                                      "role": "ADMIN"
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
            @Parameter(name = "userId", description = "권한을 부여할 사용자 ID", example = "2", required = true)
    })
    public ResponseEntity<UserEditResponse> grantAdminRole(@PathVariable("userId") Long userId) {
        log.info("admin/users/{userId}/roles: {}", userId);
        UserEditResponse response = adminService.grantAdminRole(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
