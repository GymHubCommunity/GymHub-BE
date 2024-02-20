package com.example.temp.machine.dto.response;

import java.util.List;

public record MachineSearchAllResponse(
    List<MachineSummary> results
) {

}
