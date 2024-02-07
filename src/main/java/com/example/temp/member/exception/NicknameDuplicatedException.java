package com.example.temp.member.exception;

public class NicknameDuplicatedException extends RuntimeException {

    public static final String NICKNAME_DUPLICATED_MSG = "닉네임이 중복되었습니다.";

    public NicknameDuplicatedException(Throwable cause) {
        super(NICKNAME_DUPLICATED_MSG, cause);
    }

    public NicknameDuplicatedException() {
        super(NICKNAME_DUPLICATED_MSG);
    }
}
