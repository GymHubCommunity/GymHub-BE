package com.example.temp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통
    TEST(HttpStatus.BAD_REQUEST, "테스트용 예외 메시지입니다."),

    // 인증
    AUTHENTICATED_FAIL(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    AUTHORIZED_FAIL(HttpStatus.FORBIDDEN, "인가 권한이 없는 사용자입니다."),

    // 회원
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당되는 회원을 찾을 수 없습니다."),
    MEMBER_ALREADY_REGISTER(HttpStatus.BAD_REQUEST, "이미 가입이 완료된 회원입니다."),

    // 팔로우
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "두 회원 간에 팔로우 관계를 찾을 수 없습니다."),
    FOLLOW_SELF_FAIL(HttpStatus.BAD_REQUEST, "자기 자신을 팔로우할 수 없습니다."),
    FOLLOW_ALREADY_RELATED(HttpStatus.BAD_REQUEST, "둘 사이에는 이미 관계가 존재합니다."),
    FOLLOW_STATUS_CHANGE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "입력된 팔로우 상태로는 변경이 불가능합니다."),
    FOLLOW_NOT_PENDING(HttpStatus.BAD_REQUEST, "팔로우의 상태가 PENDING이 아니므로, 해당 요청을 수행할 수 없습니다."),
    FOLLOW_INACTIVE(HttpStatus.BAD_REQUEST, "해당 Follow는 비활성화된 상태이기 때문에, 해당 요청을 수행할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
