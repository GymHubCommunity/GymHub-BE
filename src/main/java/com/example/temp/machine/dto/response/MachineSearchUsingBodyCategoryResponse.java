package com.example.temp.machine.dto.response;

import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.domain.Machine;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record MachineSearchUsingBodyCategoryResponse(
    List<MachineSearchElementAboutBodyPart> parts
) {
    public static MachineSearchUsingBodyCategoryResponse of(Map<BodyPart, List<Machine>> hash) {
        List<MachineSearchElementAboutBodyPart> parts = new ArrayList<>();
        for (Entry<BodyPart, List<Machine>> entry : hash.entrySet()) {
            parts.add(MachineSearchElementAboutBodyPart.of(entry.getKey(), entry.getValue()));
        }
        return new MachineSearchUsingBodyCategoryResponse(parts);
    }

    public record MachineSearchElementAboutBodyPart(
        BodyPart name,
        List<MachineSummary> machines
    ) {

        static MachineSearchElementAboutBodyPart of(BodyPart bodyPart, List<Machine> machines) {
            List<MachineSummary> summaries = machines.stream()
                .map(MachineSummary::from)
                .toList();
            return new MachineSearchElementAboutBodyPart(bodyPart, summaries);
        }
    }
}
