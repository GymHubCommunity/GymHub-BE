package com.example.temp.common.exception;

public class ExceptionSenderNotWorkingException extends RuntimeException {

    public static final String EXCEPTION_SENDER_NOT_WORKING_MSG = "ExceptionSender 객체가 정상적으로 동작하지 않습니다.";

    public ExceptionSenderNotWorkingException(Throwable cause) {
        super(EXCEPTION_SENDER_NOT_WORKING_MSG, cause);
    }

    public ExceptionSenderNotWorkingException() {
        super(EXCEPTION_SENDER_NOT_WORKING_MSG);
    }
}
