package com.example.temp.comment.dto.response;

import com.example.temp.comment.domain.Comment;
import com.example.temp.post.dto.response.WriterInfo;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentElementResponse(
    Long commentId,
    WriterInfo writerInfo,
    String content,
    LocalDateTime registeredAt
) {

    public static CommentElementResponse from(Comment comment) {
        return CommentElementResponse.builder()
            .commentId(comment.getId())
            .writerInfo(WriterInfo.from(comment.getMember()))
            .content(comment.getContent())
            .registeredAt(comment.getRegisteredAt())
            .build();
    }
}
