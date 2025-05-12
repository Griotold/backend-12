package com.sparta.backend_12.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    USER("일반 사용자"),
    ADMIN("관리자"),
    ;
    private final String description;
}
