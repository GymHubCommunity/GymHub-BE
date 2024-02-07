package com.example.temp.member.infrastructure.nickname;

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
public class Nickname {

    private String nickname;

    @Builder
    private Nickname(String nickname) {
        this.nickname = nickname;
    }

    public static Nickname create(String nickname) {
        return Nickname.builder()
            .nickname(nickname)
            .build();
    }
}
