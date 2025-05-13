package com.sparta.backend_12.presentation.controller;

import com.sparta.backend_12.application.dto.UserEditResponse;
import com.sparta.backend_12.application.dto.UserLoginResponse;
import com.sparta.backend_12.application.dto.UserSignupResponse;
import com.sparta.backend_12.application.service.AuthService;
import com.sparta.backend_12.presentation.dto.UserLoginRequest;
import com.sparta.backend_12.presentation.dto.UserSignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
