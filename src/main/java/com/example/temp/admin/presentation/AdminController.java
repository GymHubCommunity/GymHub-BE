package com.example.temp.admin.presentation;

import com.example.temp.admin.dto.request.MachineBulkCreateRequest;
import com.example.temp.admin.dto.request.MachineCreateRequest;
import com.example.temp.machine.application.MachineService;
import com.example.temp.machine.dto.response.MachineCreateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MachineService machineService;

    @PostMapping("/machines")
    public ResponseEntity<MachineCreateResponse> createMachine(MachineCreateRequest request) {
        MachineCreateResponse response = machineService.createMachine(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/machines/bulk")
    public ResponseEntity<List<MachineCreateResponse>> createMachinesBulk(MachineBulkCreateRequest request) {
        List<MachineCreateResponse> response = machineService.createMachinesBulk(request);
        return ResponseEntity.ok(response);
    }
}
