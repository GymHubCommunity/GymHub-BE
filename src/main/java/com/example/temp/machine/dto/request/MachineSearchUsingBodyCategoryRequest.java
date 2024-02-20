package com.example.temp.machine.dto.request;

import com.example.temp.machine.domain.BodyPart.BodyCategory;

public record MachineSearchUsingBodyCategoryRequest(
    BodyCategory bodyCategory
) {

}
