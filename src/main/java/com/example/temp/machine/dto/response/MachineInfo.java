package com.example.temp.machine.dto.response;

import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.domain.Machine;
import com.example.temp.machine.domain.MachineBodyPart;
import java.util.List;

public record MachineInfo(
    Long id,
    String name,
    List<BodyPart> bodyParts) {

    public static MachineInfo from(Machine machine) {
        return new MachineInfo(machine.getId(), machine.getName(), getBodyParts(machine));
    }

    private static List<BodyPart> getBodyParts(Machine machine) {
        return machine.getMachineBodyParts().stream()
            .map(MachineBodyPart::getBodyPart)
            .toList();
    }
}
