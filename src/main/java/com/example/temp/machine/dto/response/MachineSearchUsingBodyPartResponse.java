package com.example.temp.machine.dto.response;

import java.util.List;

public record MachineSearchUsingBodyPartResponse(
    List<MachineInfo> machines
) {

    public static MachineSearchUsingBodyPartResponse create(List<MachineInfo> machines) {
        return new MachineSearchUsingBodyPartResponse(machines);
    }
}
