package com.example.temp.machine.dto.request;

import com.example.temp.machine.domain.BodyPart;
import java.util.List;

public record MachineCreateRequest(
    String name,
    List<BodyPart> bodyParts
) {

}
