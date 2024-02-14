package com.example.temp.machine.domain;

import lombok.Getter;

@Getter
public enum BodyPart {

    BACK("등"),
    SHOULDER("어깨"),
    LEG("다리"),
    CARDIO("유산소");

    private final String text;

    BodyPart(String text) {
        this.text = text;
    }
}
