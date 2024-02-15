package com.example.temp.hashtag.domain;

import com.example.temp.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hashtags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = true)
public class Hashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @Builder
    public Hashtag(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (name.matches("^#[\\w가-힣]+$")) {
            throw new IllegalArgumentException("지원하지 않는 해시태그 형식입니다.");
        }
    }
}
