package com.example.temp.post.presentation;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.UserContext;
import com.example.temp.post.application.PostService;
import com.example.temp.post.dto.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberPostController {

    private final PostService postService;

    @GetMapping("/members/{memberId}/posts")
    public ResponseEntity<PostResponse> getPostsByMember(
        @PathVariable("memberId") Long memberId,
        @Login UserContext userContext,
        @PageableDefault(sort = "registeredAt", direction = DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(postService.findPostsByMember(memberId, userContext, pageable));
    }
}
