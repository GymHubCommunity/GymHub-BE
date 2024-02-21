package com.example.temp.comment.domain;


import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Message {

    private static final int MAX_MESSAGE_LENGTH = 200;

    @NotBlank
    @Column(name = "content", nullable = false)
    private String value;

    @Builder
    private Message(String value) {
        validate(value);
        this.value = value;
    }

    public static Message create(String value) {
        return Message.builder()
            .value(value)
            .build();
    }

    private void validate(String value) {
        if (value.length() > MAX_MESSAGE_LENGTH) {
            throw new ApiException(ErrorCode.MESSAGE_TOO_LONG);
        }
    }
}
