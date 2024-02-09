package com.example.temp.member.infrastructure.nickname;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import jakarta.persistence.Column;
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
public class Nickname {

    public static final int NICKNAME_MAX_LENGTH = 12;
    public static final int NICKNAME_MIN_LENGTH = 2;
    public static final String NICKNAME_PATTERN = "^[가-힣a-zA-Z0-9]*$";

    @Column(name = "nickname", unique = true, nullable = false)
    private String value;

    @Builder
    private Nickname(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        Objects.requireNonNull(value);
        if (value.length() < NICKNAME_MIN_LENGTH) {
            throw new ApiException(ErrorCode.NICKNAME_TOO_SHORT);
        }
        if (value.length() > NICKNAME_MAX_LENGTH) {
            throw new ApiException(ErrorCode.NICKNAME_TOO_LONG);
        }
        if (!value.matches(NICKNAME_PATTERN)) {
            throw new ApiException(ErrorCode.NICKNAME_PATTERN_MISMATCH);
        }
    }

    public static Nickname create(String value) {
        return Nickname.builder()
            .value(value)
            .build();
    }
}
