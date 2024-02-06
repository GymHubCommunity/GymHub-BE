package com.example.temp.follow.domain;

import java.util.List;

public enum FollowStatus {
    APPROVED("팔로우가 승인된 상태"),
    PENDING("팔로우 요청이 승인되지 않은 상태"),
    REJECTED("팔로우 요청을 거절한 상태"),
    CANCELED("기존 팔로우를 취소한 상태");

    private final String text;

    FollowStatus(String text) {
        this.text = text;
    }

    public boolean isActive() {
        return List.of(APPROVED, PENDING).contains(this);
    }
}
