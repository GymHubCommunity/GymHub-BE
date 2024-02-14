package com.example.temp.post.dto.request;

import com.example.temp.member.domain.Member;
import com.example.temp.post.domain.Content;
import com.example.temp.post.domain.Post;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;

public record PostCreateRequest(
    String content,
    @Nullable
    List<String> imageUrl
) {

    public Post toEntity(Member member, LocalDateTime registeredAt) {
        return Post.builder()
            .member(member)
            .content(Content.create(content))
            .registeredAt(registeredAt)
            .build();
    }
}
