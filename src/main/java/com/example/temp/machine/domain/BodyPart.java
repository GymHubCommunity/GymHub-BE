package com.example.temp.machine.domain;

import static com.example.temp.machine.domain.BodyPart.BodyCategory.LOWER;
import static com.example.temp.machine.domain.BodyPart.BodyCategory.UPPER;
import static com.example.temp.machine.domain.BodyPart.BodyCategory.WHOLE;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    CARDIO("유산소", WHOLE),
    ETC("기타", null);

    private final String text;
    private final BodyCategory category;

    BodyPart(String text, BodyCategory category) {
        this.text = text;
        this.category = category;
    }

    public static List<BodyPart> findAllBelongTo(BodyCategory category) {
        Objects.requireNonNull(category);
        return Arrays.stream(BodyPart.values())
            .filter(bodyPart -> bodyPart.isBelongTo(category))
            .toList();
    }

    /**
     * 해당 BodyPart가 category에 포함되는지 확인합니다.
     */
    private boolean isBelongTo(BodyCategory category) {
        Objects.requireNonNull(category);
        return Objects.equals(getCategory(), category);
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
