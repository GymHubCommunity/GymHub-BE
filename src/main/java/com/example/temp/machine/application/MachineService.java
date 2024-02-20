package com.example.temp.machine.application;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.domain.BodyPart.BodyCategory;
import com.example.temp.machine.domain.Machine;
import com.example.temp.machine.domain.MachineBodyPart;
import com.example.temp.machine.domain.MachineRepository;
import com.example.temp.machine.dto.request.MachineBulkCreateRequest;
import com.example.temp.machine.dto.request.MachineCreateRequest;
import com.example.temp.machine.dto.request.MachineSearchUsingBodyPartRequest;
import com.example.temp.machine.dto.response.MachineCreateResponse;
import com.example.temp.machine.dto.response.MachineInfo;
import com.example.temp.machine.dto.response.MachineSearchUsingBodyCategoryResponse;
import com.example.temp.machine.dto.response.MachineSummary;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MachineService {

    public static final int MAX_BODY_SIZE_PER_MACHINE = 1;

    private final MachineRepository machineRepository;

    @Transactional
    public MachineCreateResponse createMachine(MachineCreateRequest request) {
        if (machineRepository.existsByName(request.name())) {
            throw new ApiException(ErrorCode.MACHINE_ALREADY_REGISTER);
        }
        if (request.bodyParts().size() > MAX_BODY_SIZE_PER_MACHINE) {
            throw new ApiException(ErrorCode.MACHINE_MATCH_ONLY_ONE_BODY_PART);
        }
        Machine machine = Machine.create(request.name(), request.bodyParts());
        Machine savedMachine = machineRepository.save(machine);
        return MachineCreateResponse.from(savedMachine);
    }

    @Transactional
    public List<MachineCreateResponse> createMachinesBulk(MachineBulkCreateRequest request) {
        return request.machines().stream()
            .map(this::createMachine)
            .toList();
    }

    public MachineSearchUsingBodyCategoryResponse searchUsingBodyCategory(BodyCategory category) {
        List<BodyPart> bodyParts = BodyPart.findAllBelongTo(category);
        List<Machine> machines = machineRepository.findAllBelongTo(bodyParts);

        Map<BodyPart, List<Machine>> hash = convertListToMapByBodyPart(bodyParts, machines);
        return MachineSearchUsingBodyCategoryResponse.of(hash);
    }

    private Map<BodyPart, List<Machine>> convertListToMapByBodyPart(List<BodyPart> bodyParts, List<Machine> machines) {
        Map<BodyPart, List<Machine>> hash = createMapByBodyPart(bodyParts);
        for (Machine machine : machines) {
            addMachineIntoBodyPartMap(machine, hash);
        }
        return hash;
    }

    private void addMachineIntoBodyPartMap(Machine machine, Map<BodyPart, List<Machine>> hash) {
        List<MachineBodyPart> machineBodyPartList = machine.getMachineBodyParts();
        for (MachineBodyPart machineBodyPart : machineBodyPartList) {
            hash.get(machineBodyPart.getBodyPart()).add(machine);
        }
    }

    private Map<BodyPart, List<Machine>> createMapByBodyPart(List<BodyPart> bodyParts) {
        Map<BodyPart, List<Machine>> hash = new EnumMap<>(BodyPart.class);
        for (BodyPart bodyPart : bodyParts) {
            hash.put(bodyPart, new ArrayList<>());
        }
        return hash;
    }


    public List<MachineSummary> searchAll() {
        return machineRepository.findAll().stream()
            .map(MachineSummary::from)
            .toList();
    }

    /**
     * @deprecated 설계가 변경됨에 따라 사용하지 않게 되었습니다. 추후 삭제될 예정입니다.
     */
    @Deprecated(since = "first release", forRemoval = true)
    public List<MachineInfo> searchUsingBodyPart(MachineSearchUsingBodyPartRequest request) {
        return machineRepository.findAllByBodyPart(request.bodyPart()).stream()
            .map(MachineInfo::from)
            .toList();
    }
}
