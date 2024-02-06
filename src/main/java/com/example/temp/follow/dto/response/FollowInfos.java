package com.example.temp.follow.dto.response;

import java.util.List;

public record FollowInfos(
    List<FollowInfo> follows
) {

    public static FollowInfos from(List<FollowInfo> infos) {
        return new FollowInfos(infos);
    }
}
