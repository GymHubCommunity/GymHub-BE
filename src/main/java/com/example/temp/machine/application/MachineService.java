package com.example.temp.machine.application;

import com.example.temp.admin.dto.request.BodyPartCreateRequest;
import com.example.temp.admin.dto.request.MachineCreateRequest;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.domain.BodyPartRepository;
import com.example.temp.machine.domain.Machine;
import com.example.temp.machine.domain.MachineRepository;
import com.example.temp.machine.dto.response.BodyPartCreateResponse;
import com.example.temp.machine.dto.response.MachineCreateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MachineService {

    public static final int MAX_BODY_SIZE_PER_MACHINE = 1;

    private final MachineRepository machineRepository;
    private final BodyPartRepository bodyPartRepository;

    @Transactional
    public MachineCreateResponse createMachine(MachineCreateRequest request) {
        if (machineRepository.existsByName(request.name())) {
            throw new ApiException(ErrorCode.MACHINE_ALREADY_REGISTER);
        }
        if (request.bodyParts().size() > MAX_BODY_SIZE_PER_MACHINE) {
            throw new ApiException(ErrorCode.MACHINE_MATCH_ONLY_ONE_BODY_PART);
        }
        List<BodyPart> bodyParts = bodyPartRepository.findAllByNameIn(request.bodyParts());
        if (request.bodyParts().size() != bodyParts.size()) {
            throw new ApiException(ErrorCode.MACHINE_MAPPED_INVALID_BODY_PART);
        }

        Machine machine = Machine.create(request.name(), bodyParts);
        Machine savedMachine = machineRepository.save(machine);
        return MachineCreateResponse.from(savedMachine);
    }

    @Transactional
    public BodyPartCreateResponse createBodyPart(BodyPartCreateRequest request) {
        if (bodyPartRepository.existsByName(request.name())) {
            throw new ApiException(ErrorCode.BODY_PART_ALREADY_REGISTER);
        }
        BodyPart bodyPart = BodyPart.create(request.name());
        BodyPart savedEntity = bodyPartRepository.save(bodyPart);
        return BodyPartCreateResponse.from(savedEntity);
    }
}
