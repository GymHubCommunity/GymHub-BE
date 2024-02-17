package com.example.temp.auth.domain;

import lombok.Getter;

@Getter
public enum Role {
    NORMAL("일반 사용자"),
    ADMIN("어드민");

    private final String text;

    Role(String text) {
        this.text = text;
    }
}
