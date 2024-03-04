package com.example.temp.common.infrastructure.exceptionsender;

import com.example.temp.common.exception.ExceptionInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ExceptionSender {

    void send(ExceptionInfo exceptionInfo) throws JsonProcessingException;
}
