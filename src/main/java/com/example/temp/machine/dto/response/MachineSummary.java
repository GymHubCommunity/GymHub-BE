package com.example.temp.machine.dto.response;

import com.example.temp.machine.domain.Machine;

public record MachineSummary(
    long id,
    String name
) {

    public static MachineSummary from(Machine machine) {
        return new MachineSummary(machine.getId(), machine.getName());
    }
}
