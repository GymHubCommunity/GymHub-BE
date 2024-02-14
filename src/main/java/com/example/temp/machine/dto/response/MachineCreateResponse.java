package com.example.temp.machine.dto.response;

import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.domain.Machine;
import com.example.temp.machine.domain.MachineBodyPart;
import java.util.List;

public record MachineCreateResponse(
    String name,
    List<String> bodyParts
) {

    public static MachineCreateResponse from(Machine machine) {
        List<String> bodyParts = machine.getMachineBodyParts().stream()
            .map(MachineBodyPart::getBodyPart)
            .map(BodyPart::getName)
            .toList();
        return new MachineCreateResponse(machine.getName(), bodyParts);
    }
}
