package com.example.temp.post.dto.response;

import com.example.temp.post.domain.Post;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PostElementResponse(
    WriterInfo writerInfo,
    String content,
    String imageUrl,
    LocalDateTime createdAt
) {

    public static PostElementResponse from(Post post) {
        return PostElementResponse.builder()
            .writerInfo(WriterInfo.from(post.getMember()))
            .content(post.getContent())
            .imageUrl(post.getImageUrl())
            .createdAt(post.getCreatedAt())
            .build();
    }
}
