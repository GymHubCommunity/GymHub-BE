package com.example.temp.common.entity;

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
@SuppressWarnings("java:S1700")
public class Email {

    private String email;

    @Builder
    private Email(String email) {
        this.email = email;
    }

    public static Email create(String email) {
        return Email.builder()
            .email(email)
            .build();
    }
}
