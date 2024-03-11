package com.example.temp.common.exception;

public class ExceptionSenderNotWorkingException extends RuntimeException {

    public ExceptionSenderNotWorkingException(Throwable cause) {
        super("ExceptionSender 객체가 정상적으로 동작하지 않습니다.", cause);
    }

}
