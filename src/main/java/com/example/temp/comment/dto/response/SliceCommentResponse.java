package com.example.temp.comment.dto.response;

import com.example.temp.comment.domain.Comment;
import java.util.List;
import org.springframework.data.domain.Slice;

public record SliceCommentResponse(
    List<CommentElementResponse> comments,
    boolean hasNext
) {

    public static SliceCommentResponse from(Slice<Comment> comments) {
        List<CommentElementResponse> commentElements = comments.stream()
            .map(CommentElementResponse::from)
            .toList();
        return new SliceCommentResponse(commentElements, comments.hasNext());
    }
}
