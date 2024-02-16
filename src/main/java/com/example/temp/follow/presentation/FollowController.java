package com.example.temp.follow.presentation;

import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.UserContext;
import com.example.temp.follow.application.FollowService;
import com.example.temp.follow.dto.response.FollowInfo;
import com.example.temp.follow.dto.response.FollowInfoResult;
import com.example.temp.follow.dto.response.FollowInfos;
import com.example.temp.follow.dto.response.FollowResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/members/{memberId}/followings")
    public ResponseEntity<FollowInfoResult> getFollowings(@Login UserContext userContext, @PathVariable Long memberId,
        @RequestParam Pageable pageable) {
        FollowInfoResult result = followService.getFollowings(userContext, memberId, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/members/{memberId}/followers")
    public ResponseEntity<FollowInfos> getFollowers(@Login UserContext userContext, @PathVariable Long memberId) {
        List<FollowInfo> followInfos = followService.getFollowers(userContext, memberId);
        return ResponseEntity.ok(FollowInfos.from(followInfos));
    }

    @PostMapping("/members/{memberId}/follow")
    public ResponseEntity<FollowResponse> follow(@Login UserContext userContext, @PathVariable Long memberId) {
        FollowResponse response = followService.follow(userContext, memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/members/{memberId}/unfollow")
    public ResponseEntity<Void> unfollow(@Login UserContext userContext, @PathVariable Long memberId) {
        followService.unfollow(userContext, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/follows/{followId}")
    public ResponseEntity<Void> acceptFollowRequest(@Login UserContext userContext, @PathVariable Long followId) {
        followService.acceptFollowRequest(userContext, followId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/follows/{followId}")
    public ResponseEntity<Void> rejectFollowRequest(@Login UserContext userContext, @PathVariable Long followId) {
        followService.rejectFollowRequest(userContext, followId);
        return ResponseEntity.noContent().build();
    }
}
