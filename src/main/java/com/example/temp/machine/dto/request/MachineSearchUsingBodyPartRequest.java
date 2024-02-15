package com.example.temp.machine.dto.request;

import com.example.temp.machine.domain.BodyPart;

public record MachineSearchUsingBodyPartRequest(
    BodyPart bodyPart
) {

}
