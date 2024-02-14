package com.example.temp.machine.application;

import com.example.temp.admin.dto.request.MachineCreateRequest;
import com.example.temp.machine.domain.MachineRepository;
import com.example.temp.machine.dto.response.MachineCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;

    @Transactional
    public MachineCreateResponse create(MachineCreateRequest request) {

        return null;
    }
}
