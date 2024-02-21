package com.example.temp.comment.presentation;

import com.example.temp.comment.application.CommentService;
import com.example.temp.comment.dto.request.CommentCreateRequest;
import com.example.temp.comment.dto.response.CommentCreateResponse;
import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentCreateResponse> createComment(@PathVariable Long postId,
        @Login UserContext userContext,
        @RequestBody CommentCreateRequest commentCreateRequest) {
        Long commentId = commentService.createComment(postId, userContext, commentCreateRequest);
        return ResponseEntity.ok(CommentCreateResponse.create(commentId));
    }
}
