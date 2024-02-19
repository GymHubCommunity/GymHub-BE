package com.example.temp.post.presentation;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.UserContext;
import com.example.temp.post.application.PostService;
import com.example.temp.post.dto.request.PostCreateRequest;
import com.example.temp.post.dto.request.PostUpdateRequest;
import com.example.temp.post.dto.response.SlicePostResponse;
import com.example.temp.post.dto.response.PostCreateResponse;
import com.example.temp.post.dto.response.PostDetailResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        return ResponseEntity.ok(postService.createPost(userContext, postCreateRequest, registeredAt));
    }

    @GetMapping
    public ResponseEntity<SlicePostResponse> getFollowingPosts(@Login UserContext userContext,
        @PageableDefault(sort = "registeredAt", direction = DESC) Pageable pageable) {
        SlicePostResponse posts = postService.findPostsFromFollowings(userContext, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long postId, @Login UserContext userContext) {
        return ResponseEntity.ok(postService.findPost(postId, userContext));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId, @Login UserContext userContext,
        @Validated @RequestBody PostUpdateRequest request) {
        postService.updatePost(postId, userContext, request);
        return ResponseEntity.noContent().build();
    }
}
