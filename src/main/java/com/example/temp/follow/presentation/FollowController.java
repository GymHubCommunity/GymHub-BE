package com.example.temp.follow.presentation;

import com.example.temp.follow.application.FollowService;
import com.example.temp.follow.response.FollowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    /**
     * 로그인된 사용자의 ID를 모킹했습니다. 현재 access token을 통해 사용자의 id를 받아오는 로직이 만들어지지 않아, 임시로 사용중입니다.
     */
    public static final long AUTHENTICATED_MEMBER_ID = 1L;

    private final FollowService followService;

    @PostMapping("/members/{memberId}/follow")
    public ResponseEntity<FollowResponse> follow(@PathVariable Long memberId) {
        FollowResponse response = followService.follow(AUTHENTICATED_MEMBER_ID, memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/members/{memberId}/unfollow")
    public ResponseEntity<Void> unfollow(@PathVariable Long memberId) {
        followService.unfollow(AUTHENTICATED_MEMBER_ID, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/follows/{followId}")
    public ResponseEntity<Void> acceptFollowRequest(@PathVariable Long followId) {
        followService.acceptFollowRequest(AUTHENTICATED_MEMBER_ID, followId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/follows/{followId}")
    public ResponseEntity<Void> rejectFollowRequest(@PathVariable Long followId) {
        followService.rejectFollowRequest(AUTHENTICATED_MEMBER_ID, followId);
        return ResponseEntity.noContent().build();
    }
}
