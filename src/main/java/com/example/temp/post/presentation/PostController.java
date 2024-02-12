package com.example.temp.post.presentation;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.UserContext;
import com.example.temp.post.application.PostService;
import com.example.temp.post.dto.request.PostCreateRequest;
import com.example.temp.post.dto.response.PagePostResponse;
import com.example.temp.post.dto.response.PostCreateResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostCreateResponse> createPost(@Login UserContext userContext,
        @RequestBody PostCreateRequest postCreateRequest) {
        LocalDateTime registeredAt = LocalDateTime.now();
        return ResponseEntity.ok(postService.create(userContext, postCreateRequest, registeredAt));
    }

    @GetMapping
    public ResponseEntity<PagePostResponse> getFollowingPosts(@Login UserContext userContext,
        @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable) {
        PagePostResponse posts = postService.findPostsFromFollowings(userContext, pageable);
        return ResponseEntity.ok(posts);
    }
}
