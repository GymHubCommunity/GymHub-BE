package com.example.temp.machine.dto.response;

import com.example.temp.machine.domain.Machine;
import com.example.temp.machine.domain.MachineBodyPart;
import java.util.List;

public record MachineCreateResponse(
    Long id,
    String name,
    List<String> bodyParts
) {

    public static MachineCreateResponse from(Machine machine) {
        List<String> bodyParts = machine.getMachineBodyParts().stream()
            .map(MachineBodyPart::getBodyPart)
            .map(Enum::name)
            .toList();
        return new MachineCreateResponse(machine.getId(), machine.getName(), bodyParts);
    }
}
