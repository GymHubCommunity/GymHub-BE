package com.example.temp.machine.presentation;

import com.example.temp.machine.application.MachineService;
import com.example.temp.machine.dto.request.MachineSearchUsingBodyPartRequest;
import com.example.temp.machine.dto.response.MachineInfo;
import com.example.temp.machine.dto.response.MachineSearchUsingBodyPartResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/machines")
public class MachineController {

    private final MachineService machineService;

    @GetMapping
    public ResponseEntity<MachineSearchUsingBodyPartResponse> searchUsingBodyPart(
        MachineSearchUsingBodyPartRequest request) {
        List<MachineInfo> machines = machineService.searchUsingBodyPart(request);
        return ResponseEntity.ok(new MachineSearchUsingBodyPartResponse(machines));
    }

}