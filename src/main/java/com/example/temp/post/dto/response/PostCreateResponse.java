package com.example.temp.post.dto.response;

import com.example.temp.post.domain.Post;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record PostCreateResponse(
    Long id,
    WriterInfo writerInfo,
    String content,
    List<String> postImages,
    LocalDateTime registeredAt
) {

    public static PostCreateResponse from(Post savedPost) {
        return PostCreateResponse.builder()
            .id(savedPost.getId())
            .writerInfo(WriterInfo.from(savedPost.getMember()))
            .content(savedPost.getContent())
            .postImages(urlFromPostImage(savedPost))
            .registeredAt(savedPost.getRegisteredAt())
            .build();
    }

    private static List<String> urlFromPostImage(Post savedPost) {
        return savedPost.getPostImages().stream()
            .map(postImage -> postImage.getImage().getUrl())
            .toList();
    }
}
