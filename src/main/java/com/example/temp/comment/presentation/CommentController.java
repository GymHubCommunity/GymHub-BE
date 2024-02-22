package com.example.temp.comment.presentation;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.example.temp.comment.application.CommentService;
import com.example.temp.comment.dto.request.CommentCreateRequest;
import com.example.temp.comment.dto.response.SliceCommentResponse;
import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.CreatedResponse;
import com.example.temp.common.dto.UserContext;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CreatedResponse> createComment(@PathVariable Long postId,
        @Login UserContext userContext,
        @RequestBody CommentCreateRequest commentCreateRequest) {
        LocalDateTime registeredAt = LocalDateTime.now();
        Long commentId = commentService.createComment(postId, userContext, commentCreateRequest, registeredAt);
        return ResponseEntity.ok(CreatedResponse.of(commentId));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<SliceCommentResponse> getCommentsByPost(
        @PathVariable Long postId,
        @Login UserContext userContext,
        @PageableDefault(sort = "registeredAt", direction = DESC) Pageable pageable
    ) {
        SliceCommentResponse sliceComments = commentService.findCommentsByPost(postId, userContext, pageable);
        return ResponseEntity.ok(sliceComments);
    }
}
