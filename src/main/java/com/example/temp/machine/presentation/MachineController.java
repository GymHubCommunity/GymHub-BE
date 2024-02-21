package com.example.temp.machine.presentation;

import com.example.temp.machine.application.MachineService;
import com.example.temp.machine.domain.BodyPart.BodyCategory;
import com.example.temp.machine.dto.response.MachineSearchAllResponse;
import com.example.temp.machine.dto.response.MachineSearchUsingBodyCategoryResponse;
import com.example.temp.machine.dto.response.MachineSummary;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/machines")
public class MachineController {

    private final MachineService machineService;

    @GetMapping("/search")
    public ResponseEntity<MachineSearchUsingBodyCategoryResponse> searchUsingBodyCategory(
        @RequestParam("category") BodyCategory category) {
        MachineSearchUsingBodyCategoryResponse response = machineService.searchUsingBodyCategory(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/all")
    public ResponseEntity<MachineSearchAllResponse> searchAll() {
        List<MachineSummary> results = machineService.searchAll();
        return ResponseEntity.ok(new MachineSearchAllResponse(results));
    }

}
