package com.example.temp.admin.dto.request;

import java.util.List;

public record MachineCreateRequest(
    String name,
    List<String> bodyParts
) {
}
