package com.example.temp.follow.dto.response;

import com.example.temp.follow.domain.Follow;
import java.util.List;
import org.springframework.data.domain.Slice;

public record FollowInfoResult(
    List<FollowInfo> followInfos,
    boolean hasNext
) {

    public static FollowInfoResult from(Slice<Follow> follows) {
        List<FollowInfo> followInfos = follows.stream()
            .map(follow -> FollowInfo.of(follow.getTo(), follow.getId()))
            .toList();
        boolean hasNext = follows.hasNext();
        return new FollowInfoResult(followInfos, hasNext);
    }
}
