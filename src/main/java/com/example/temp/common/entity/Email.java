package com.example.temp.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Email {

    @Column(name = "email", nullable = false)
    private String value;

    @Builder
    private Email(String value) {
        this.value = value;
    }

    public static Email create(String value) {
        return Email.builder()
            .value(value)
            .build();
    }
}
