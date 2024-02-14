package com.example.temp.machine.application;

import com.example.temp.admin.dto.request.MachineCreateRequest;
import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.domain.BodyPartRepository;
import com.example.temp.machine.domain.Machine;
import com.example.temp.machine.domain.MachineRepository;
import com.example.temp.machine.dto.response.MachineCreateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;
    private final BodyPartRepository bodyPartRepository;

    @Transactional
    public MachineCreateResponse create(MachineCreateRequest request) {
        List<BodyPart> bodyParts = bodyPartRepository.findAllByNames(request.bodyParts());
        // 사이즈 안맞으면??

        Machine machine = Machine.create(request.name(), bodyParts);
        Machine savedMachine = machineRepository.save(machine);
        return MachineCreateResponse.from(savedMachine);
    }
}
