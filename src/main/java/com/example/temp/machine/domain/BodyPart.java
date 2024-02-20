package com.example.temp.machine.domain;

import static com.example.temp.machine.domain.BodyPart.BodyCategory.LOWER;
import static com.example.temp.machine.domain.BodyPart.BodyCategory.UPPER;
import static com.example.temp.machine.domain.BodyPart.BodyCategory.WHOLE;

import lombok.Getter;

@Getter
public enum BodyPart {

    CHEST("가슴", UPPER),
    BACK("등", UPPER),
    SHOULDER("어깨", UPPER),
    TRICEPS("삼두", UPPER),
    BICEPS("이두", UPPER),
    FOREARM("전완근", UPPER),
    LEG("다리", LOWER),
    HIP("엉덩이", LOWER),
    CORE("코어", WHOLE),
    CARDIO("유산소", WHOLE);

    private final String text;
    private final BodyCategory category;

    BodyPart(String text, BodyCategory category) {
        this.text = text;
        this.category = category;
    }

    @Getter
    public enum BodyCategory {

        UPPER("상체"),
        LOWER("하체"),
        WHOLE("전신");

        private final String text;

        BodyCategory(String text) {
            this.text = text;
        }
    }
}
