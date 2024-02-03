package com.example.temp.follow.domain;

public enum FollowStatus {
    SUCCESS("성공"),
    PENDING("보류"),
    REJECTED("거절"),
    CANCELED("취소");

    private final String text;

    FollowStatus(String text) {
        this.text = text;
    }
}
