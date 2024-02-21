package com.example.temp.machine.dto.request;

import com.example.temp.machine.domain.BodyPart;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MachineCreateRequest(
    @NotBlank
    String name,

    @NotNull
    List<BodyPart> bodyParts
) {

}
