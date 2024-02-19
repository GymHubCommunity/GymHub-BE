package com.example.temp.record.presentation;

import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.UserContext;
import com.example.temp.record.application.RecordService;
import com.example.temp.record.dto.request.RecordCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/records")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<Void> create(@Login UserContext userContext, RecordCreateRequest request) {
        long createdId = recordService.create(request);
        return null;
    }
}
