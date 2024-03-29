package com.example.temp.post.presentation;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.CreatedResponse;
import com.example.temp.common.dto.UserContext;
import com.example.temp.post.application.PostService;
import com.example.temp.post.dto.request.PostCreateRequest;
import com.example.temp.post.dto.request.PostUpdateRequest;
import com.example.temp.post.dto.response.PostDetailResponse;
import com.example.temp.post.dto.response.PostResponse;
import com.example.temp.post.dto.response.PostSearchResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<CreatedResponse> createPost(@Login UserContext userContext,
        @RequestBody PostCreateRequest postCreateRequest) {
        LocalDateTime registeredAt = LocalDateTime.now();
        Long postId = postService.createPost(userContext, postCreateRequest, registeredAt);
        return ResponseEntity.ok(CreatedResponse.of(postId));
    }

    @GetMapping
    public ResponseEntity<PostResponse> getFollowingPosts(@Login UserContext userContext,
        @PageableDefault(sort = "registeredAt", direction = DESC) Pageable pageable) {
        PostResponse posts = postService.findMyAndFollowingPosts(userContext, pageable);
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

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @Login UserContext userContext) {
        postService.deletePost(postId, userContext);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PostSearchResponse> getPostsByHashtag(
        @RequestParam(required = false) String hashtag,
        @Login UserContext userContext,
        @PageableDefault(sort = "registeredAt", direction = DESC) Pageable pageable) {
        PostSearchResponse posts = postService.findPostsByHashtag(hashtag, userContext, pageable);
        return ResponseEntity.ok(posts);
    }
}
