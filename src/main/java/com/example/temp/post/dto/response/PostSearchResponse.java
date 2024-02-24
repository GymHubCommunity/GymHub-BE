package com.example.temp.post.dto.response;

import com.example.temp.post.domain.Post;
import java.util.List;
import org.springframework.data.domain.Page;

public record PostSearchResponse(
    List<PostElementResponse> posts,
    long totalPostCount,
    boolean hasNext
) {

    public static PostSearchResponse from(Page<Post> posts) {
        List<PostElementResponse> postElements = posts.stream()
            .map(PostElementResponse::from)
            .toList();
        return new PostSearchResponse(postElements, posts.getTotalElements(), posts.hasNext());
    }
}
