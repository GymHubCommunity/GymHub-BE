package com.example.temp.machine.dto.response;

import com.example.temp.machine.domain.BodyPart;

public record BodyPartCreateResponse(
    Long id,
    String name
) {

    public static BodyPartCreateResponse from(BodyPart bodyPart) {
        return new BodyPartCreateResponse(bodyPart.getId(), bodyPart.getName());
    }
}
