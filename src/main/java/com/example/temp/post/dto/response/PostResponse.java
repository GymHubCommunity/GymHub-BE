package com.example.temp.post.dto.response;

import com.example.temp.post.domain.Post;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Slice;

@Builder
public record PostResponse(
    List<PostElementResponse> posts,
    boolean hasNext
) {

    public static PostResponse from(Slice<Post> posts) {
        List<PostElementResponse> postElements = posts.stream()
            .map(PostElementResponse::from)
            .toList();
        return new PostResponse(postElements, posts.hasNext());
    }
}
