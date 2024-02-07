package com.example.temp.follow.response;

import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowStatus;

public record FollowResponse(
    long id,
    FollowStatus status
) {

    public static FollowResponse from(Follow follow) {
        return new FollowResponse(follow.getId(), follow.getStatus());
    }
}
