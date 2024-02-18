package com.example.temp.post.dto.response;

import com.example.temp.post.domain.Post;
import com.example.temp.post.domain.PostHashtag;
import com.example.temp.post.domain.PostImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostCreateResponse(
    Long postId,
    WriterInfo writerInfo,
    String content,
    List<String> postImages,
    List<String> hashtags,
    LocalDateTime registeredAt
) {

    public static PostCreateResponse from(Post savedPost) {
        return PostCreateResponse.builder()
            .postId(savedPost.getId())
            .writerInfo(WriterInfo.from(savedPost.getMember()))
            .content(savedPost.getContent())
            .postImages(urlFromPostImage(savedPost.getPostImages()))
            .hashtags(hashtagFromPostHashtag(savedPost.getPostHashtags()))
            .registeredAt(savedPost.getRegisteredAt())
            .build();
    }

    private static List<String> urlFromPostImage(List<PostImage> postImages) {
        return postImages.stream()
            .map(PostImage::getImageUrl)
            .toList();
    }

    private static List<String> hashtagFromPostHashtag(List<PostHashtag> postHashtags) {
        return postHashtags.stream()
            .map(postHashtag -> postHashtag.getHashtag().getName())
            .toList();
    }
}
