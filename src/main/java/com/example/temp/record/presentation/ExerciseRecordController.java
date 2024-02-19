package com.example.temp.record.presentation;

import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.CreatedResponse;
import com.example.temp.common.dto.UserContext;
import com.example.temp.record.application.ExerciseRecordService;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/records")
public class ExerciseRecordController {

    private final ExerciseRecordService exerciseRecordService;

    @PostMapping
    public ResponseEntity<CreatedResponse> create(@Login UserContext userContext, ExerciseRecordCreateRequest request) {
        long createdId = exerciseRecordService.create(userContext, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreatedResponse.of(createdId));
    }
}
