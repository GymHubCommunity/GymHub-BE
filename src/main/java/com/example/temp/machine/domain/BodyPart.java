package com.example.temp.machine.domain;

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
@Table(name = "bodyparts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BodyPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bodypart_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Builder
    private BodyPart(String name) {
        this.name = name;
    }

    public static BodyPart create(String name) {
        return BodyPart.builder()
            .name(name)
            .build();
    }
}
