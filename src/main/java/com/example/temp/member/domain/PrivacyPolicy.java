package com.example.temp.member.domain;

public enum PrivacyPolicy {
    PUBLIC("공개 계정"),
    PRIVATE("비공개 계정");

    private final String text;

    PrivacyPolicy(String text) {
        this.text = text;
    }
}
