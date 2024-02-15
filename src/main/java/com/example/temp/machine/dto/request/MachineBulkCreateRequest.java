package com.example.temp.machine.dto.request;

import java.util.List;

public record MachineBulkCreateRequest(
    List<MachineCreateRequest> machines
) {

}