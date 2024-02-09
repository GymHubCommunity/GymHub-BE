package com.example.temp.post.dto.response;

import com.example.temp.post.domain.Content;
import com.example.temp.post.domain.Post;
import lombok.Builder;

@Builder
public record PostElementResponse(
    WriterInfo writerInfo,
    Content content,
    String imageUrl
) {

    public static PostElementResponse from(Post post) {
        return PostElementResponse.builder()
            .writerInfo(WriterInfo.from(post.getMember()))
            .content(post.getContent())
            .imageUrl(post.getImageUrl())
            .build();
    }
}
