package com.example.temp.machine.domain;

import lombok.Getter;

@Getter
public enum BodyPart {

    CHEST("가슴"),
    BACK("등"),
    SHOULDER("어깨"),
    TRICEPS("삼두"),
    BICEPS("이두"),
    FOREARM("전완근"),
    LEG("다리"),
    HIP("엉덩이"),
    CORE("코어"),
    CARDIO("유산소");

    private final String text;

    BodyPart(String text) {
        this.text = text;
    }
}
