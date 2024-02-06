package com.example.temp.member.domain;

import com.example.temp.follow.domain.FollowStatus;
import lombok.Getter;

@Getter
public enum FollowStrategy {
    EAGER("팔로우 요청 즉시 팔로우가 되는 전략", FollowStatus.APPROVED),
    LAZY("팔로우 요청이 들어오면 사용자가 확인 후 허가하는 전략", FollowStatus.PENDING);

    private final String text;
    private final FollowStatus followStatus;

    FollowStrategy(String text, FollowStatus followStatus) {
        this.text = text;
        this.followStatus = followStatus;
    }
}
