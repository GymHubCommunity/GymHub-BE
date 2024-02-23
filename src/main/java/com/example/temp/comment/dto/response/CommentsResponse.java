package com.example.temp.comment.dto.response;

import com.example.temp.comment.domain.Comment;
import java.util.List;
import org.springframework.data.domain.Slice;

public record CommentsResponse(
    List<CommentElementResponse> comments,
    boolean hasNext
) {

    public static CommentsResponse from(Slice<Comment> comments) {
        List<CommentElementResponse> commentElements = comments.stream()
            .map(CommentElementResponse::from)
            .toList();
        return new CommentsResponse(commentElements, comments.hasNext());
    }
}
