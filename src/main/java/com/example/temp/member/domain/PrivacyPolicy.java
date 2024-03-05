package com.example.temp.member.domain;

import lombok.Getter;

@Getter
public enum PrivacyPolicy {
    PUBLIC("공개 계정", FollowStrategy.EAGER),
    PRIVATE("비공개 계정", FollowStrategy.LAZY);

    private final String text;
    private final FollowStrategy defaultFollowStrategy;

    PrivacyPolicy(String text, FollowStrategy defaultFollowStrategy) {
        this.text = text;
        this.defaultFollowStrategy = defaultFollowStrategy;
    }
}
