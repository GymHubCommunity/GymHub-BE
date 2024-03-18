package com.example.temp.common.infrastructure.exceptionsender;

import com.example.temp.common.exception.ExceptionInfo;

public interface ExceptionSender {

    void send(ExceptionInfo exceptionInfo);
}
