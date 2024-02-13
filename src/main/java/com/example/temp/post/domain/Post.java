package com.example.temp.post.domain;

import com.example.temp.common.entity.BaseTimeEntity;
import com.example.temp.image.domain.Image;
import com.example.temp.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Embedded
    private Content content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<PostImage> postImages = new ArrayList<>();

    private LocalDateTime registeredAt;

    @Builder
    public Post(Member member, Content content, LocalDateTime registeredAt) {
        this.member = member;
        this.content = content;
        this.registeredAt = registeredAt;
    }

    public String getContent() {
        return content.getValue();
    }

    public String getImageUrl() {
        return postImages.stream()
            .findFirst()
            .map(PostImage::getImage)
            .map(Image::getUrl)
            .orElse(null);
    }
}
