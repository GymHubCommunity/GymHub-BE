package com.example.temp.comment.dto.response;

public record CommentCreateResponse(Long commentId) {

    public static CommentCreateResponse create(Long commentId) {
        return new CommentCreateResponse(commentId);
    }
}
