package com.example.temp.machine.presentation;

import com.example.temp.machine.application.MachineService;
import com.example.temp.machine.domain.BodyPart.BodyCategory;
import com.example.temp.machine.dto.request.MachineSearchUsingBodyPartRequest;
import com.example.temp.machine.dto.response.MachineInfo;
import com.example.temp.machine.dto.response.MachineSearchUsingBodyCategoryResponse;
import com.example.temp.machine.dto.response.MachineSearchUsingBodyPartResponse;
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

    /**
     * @deprecated 해당 메서드는 설계가 변경됨에 따라 사용하지 않게 되었습니다.
     */
    @Deprecated(since = "first release", forRemoval = true)
    public ResponseEntity<MachineSearchUsingBodyPartResponse> searchUsingBodyPart(
        MachineSearchUsingBodyPartRequest request) {
        List<MachineInfo> machines = machineService.searchUsingBodyPart(request);
        return ResponseEntity.ok(new MachineSearchUsingBodyPartResponse(machines));
    }
}
