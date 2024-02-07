package com.example.temp.member.infrastructure.nickname;

import com.example.temp.exception.ApiException;
import com.example.temp.exception.ErrorCode;
import jakarta.persistence.Embeddable;
import java.util.Objects;
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

    public static final int NICKNAME_MAX_LENGTH = 12;
    public static final int NICKNAME_MIN_LENGTH = 2;
    public static final String NICKNAME_PATTERN = "^[가-힣a-zA-Z0-9]*$";

    private String nickname;

    @Builder
    private Nickname(String nickname) {
        validate(nickname);
        this.nickname = nickname;
    }

    public void validate(String nickname) {
        Objects.requireNonNull(nickname);
        if (nickname.length() < NICKNAME_MIN_LENGTH) {
            throw new ApiException(ErrorCode.NICKNAME_TOO_SHORT);
        }
        if (nickname.length() > NICKNAME_MAX_LENGTH) {
            throw new ApiException(ErrorCode.NICKNAME_TOO_LONG);
        }
        if (!nickname.matches(NICKNAME_PATTERN)) {
            throw new ApiException(ErrorCode.NICKNAME_PATTERN_MISMATCH);
        }
    }

    public static Nickname create(String nickname) {
        return Nickname.builder()
            .nickname(nickname)
            .build();
    }
}
