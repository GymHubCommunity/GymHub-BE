package com.example.temp.member.domain;

public enum PrivacyStrategy {
    PUBLIC("공개 계정"),
    PRIVATE("비공개 계정");

    private final String text;

    PrivacyStrategy(String text) {
        this.text = text;
    }
}
