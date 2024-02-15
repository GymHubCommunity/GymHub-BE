package com.example.temp.post.domain;

import com.example.temp.hashtag.domain.Hashtag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_hashtags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Builder
    private PostHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    public static PostHashtag createPostHashtag(Hashtag hashtag) {
        return PostHashtag.builder()
            .hashtag(hashtag)
            .build();
    }

    //== 연관관계 편의 메서드 ==//
    public void addPost(Post post) {
        if (this.post != null) {
            this.post.getPostHashtags().remove(this);
        }
        this.post = post;
        post.getPostHashtags().add(this);
    }
}
