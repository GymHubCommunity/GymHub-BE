package com.example.temp.follow.response;

import com.example.temp.follow.domain.FollowStatus;

public record FollowResponse(
    long id,
    FollowStatus status
) {

}
