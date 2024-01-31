package com.example.temp.member.exception;

public class NicknameDuplicatedException extends RuntimeException {

    public static final String NICKNAME_DUPLICATED_MSG = "서버가 중복된 닉네임으로 회원 가입을 시도하고 있습니다.";

    public NicknameDuplicatedException(Throwable cause) {
        super(NICKNAME_DUPLICATED_MSG, cause);
    }

    public NicknameDuplicatedException() {
        super(NICKNAME_DUPLICATED_MSG);
    }
}
