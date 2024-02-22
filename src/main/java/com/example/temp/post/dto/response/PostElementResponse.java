package com.example.temp.post.dto.response;

import com.example.temp.post.domain.Post;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PostElementResponse(
    Long postId,
    WriterInfo writerInfo,
    String content,
    String imageUrl,
    int commentCount,
    LocalDateTime registeredAt
) {

    public static PostElementResponse from(Post post) {
        return PostElementResponse.builder()
            .postId(post.getId())
            .writerInfo(WriterInfo.from(post.getMember()))
            .content(post.getContent())
            .imageUrl(post.getImageUrl().orElse(null))
            .commentCount(post.getCommentCount())
            .registeredAt(post.getRegisteredAt())
            .build();
    }
}
