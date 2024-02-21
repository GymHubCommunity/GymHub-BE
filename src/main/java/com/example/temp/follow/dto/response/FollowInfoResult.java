package com.example.temp.follow.dto.response;

import com.example.temp.follow.domain.Follow;
import java.util.List;
import org.springframework.data.domain.Slice;

public record FollowInfoResult(
    List<FollowInfo> follows,
    boolean hasNext
) {

    /**
     * 특정 사용자가 팔로우 하고 있는(... -> To) 사용자들과 hasNext
     *
     * @param follows
     * @return
     */
    public static FollowInfoResult createFollowingsResult(Slice<Follow> follows) {
        List<FollowInfo> followInfos = follows.stream()
            .map(follow -> FollowInfo.of(follow.getTo(), follow.getId()))
            .toList();
        boolean hasNext = follows.hasNext();
        return new FollowInfoResult(followInfos, hasNext);
    }

    /**
     * 특정 사용자를 팔로우 하고 있는(from -> ...) 사용자들을 꺼냅니다.
     *
     * @param follows
     * @return
     */
    public static FollowInfoResult createFollowersResult(Slice<Follow> follows) {
        List<FollowInfo> followInfos = follows.stream()
            .map(follow -> FollowInfo.of(follow.getFrom(), follow.getId()))
            .toList();
        boolean hasNext = follows.hasNext();
        return new FollowInfoResult(followInfos, hasNext);
    }
}
