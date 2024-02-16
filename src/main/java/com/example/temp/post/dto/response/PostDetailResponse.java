package com.example.temp.post.dto.response;

import com.example.temp.post.domain.Post;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Builder;

@Builder
public record PostDetailResponse(
    Long postId,
    WriterInfo writerInfo,
    String content,
    List<String> imageUrls,
    List<String> hashtags,
    LocalDateTime registeredAt
) {

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
            .postId(post.getId())
            .writerInfo(WriterInfo.from(post.getMember()))
            .content(post.getContent())
            .imageUrls(getImageUrls(post))
            .hashtags(getHashtags(post))
            .registeredAt(post.getRegisteredAt())
            .build();
    }

    private static List<String> getImageUrls(Post post) {
        return post.getPostImages().isEmpty() ? Collections.emptyList() :
            post.getPostImages().stream()
                .map(postImage -> postImage.getImage().getUrl())
                .toList();
    }

    private static List<String> getHashtags(Post post) {
        return post.getPostHashtags().isEmpty() ? Collections.emptyList() :
            post.getPostHashtags().stream()
                .map(postHashtag -> postHashtag.getHashtag().getName())
                .toList();
    }
}
