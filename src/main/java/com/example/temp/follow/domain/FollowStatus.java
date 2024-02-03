package com.example.temp.follow.domain;

import java.util.List;

public enum FollowStatus {
    SUCCESS("성공"),
    PENDING("보류"),
    REJECTED("거절"),
    CANCELED("취소");

    private final String text;

    FollowStatus(String text) {
        this.text = text;
    }

    public boolean isValid() {
        return List.of(SUCCESS, PENDING).contains(this);
    }
}
