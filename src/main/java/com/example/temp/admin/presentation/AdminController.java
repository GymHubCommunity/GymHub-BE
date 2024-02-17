package com.example.temp.admin.presentation;

import com.example.temp.admin.application.AdminService;
import com.example.temp.admin.dto.request.AdminLoginRequest;
import com.example.temp.admin.dto.request.AdminRegisterRequest;
import com.example.temp.machine.application.MachineService;
import com.example.temp.machine.dto.request.MachineBulkCreateRequest;
import com.example.temp.machine.dto.response.MachineCreateResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MachineService machineService;

    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody AdminLoginRequest request) {
        adminService.login(request, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody AdminRegisterRequest request) {
        adminService.register(request, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/machines/bulk")
    public ResponseEntity<List<MachineCreateResponse>> createMachinesBulk(MachineBulkCreateRequest request) {
        List<MachineCreateResponse> response = machineService.createMachinesBulk(request);
        return ResponseEntity.ok(response);
    }

}
