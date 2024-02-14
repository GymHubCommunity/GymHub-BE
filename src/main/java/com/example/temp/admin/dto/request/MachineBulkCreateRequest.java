package com.example.temp.admin.dto.request;

import java.util.List;

public record MachineBulkCreateRequest(
    List<MachineCreateRequest> machines
) {

}
