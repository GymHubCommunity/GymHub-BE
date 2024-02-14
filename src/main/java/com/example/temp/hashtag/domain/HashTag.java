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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hashtags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HashTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    private String name;

    @Builder
    public HashTag(String name) {
        this.name = name;
    }
}
