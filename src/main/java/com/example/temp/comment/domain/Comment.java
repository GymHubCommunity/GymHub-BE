package com.example.temp.comment.domain;

import com.example.temp.common.entity.BaseTimeEntity;
import com.example.temp.member.domain.Member;
import com.example.temp.post.domain.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Embedded
    private Content content;

    private LocalDateTime registeredAt;

    @Builder
    private Comment(Member member, Content content, Post post, LocalDateTime registeredAt) {
        this.member = member;
        this.content = content;
        this.post = post;
        this.registeredAt = registeredAt;
    }

    public static Comment create(Member member, String content, Post post, LocalDateTime registeredAt) {
        return Comment.builder()
            .member(member)
            .content(Content.create(content))
            .post(post)
            .registeredAt(registeredAt)
            .build();
    }

    public void relatePost(Post post) {
        this.post = post;
    }

    public String getContent() {
        return this.content.getValue();
    }
}
