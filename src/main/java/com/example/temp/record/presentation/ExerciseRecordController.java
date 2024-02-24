package com.example.temp.record.presentation;

import com.example.temp.common.annotation.Login;
import com.example.temp.common.domain.period.MonthlyDatePeriod;
import com.example.temp.common.dto.CreatedResponse;
import com.example.temp.common.dto.UserContext;
import com.example.temp.record.application.ExerciseRecordService;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest;
import com.example.temp.record.dto.response.RetrievePeriodExerciseRecordsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/records")
public class ExerciseRecordController {

    private final ExerciseRecordService exerciseRecordService;

    @PostMapping
    public ResponseEntity<CreatedResponse> create(@Login UserContext userContext,
        @Validated @RequestBody ExerciseRecordCreateRequest request) {
        long createdId = exerciseRecordService.create(userContext, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreatedResponse.of(createdId));
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<Void> update(@Login UserContext userContext, @PathVariable long recordId,
        @Validated @RequestBody ExerciseRecordUpdateRequest request) {
        exerciseRecordService.update(userContext, recordId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> delete(@Login UserContext userContext, @PathVariable long recordId) {
        exerciseRecordService.delete(userContext, recordId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<RetrievePeriodExerciseRecordsResponse> retrievePeriodExerciseRecords(
        @Login UserContext userContext, @RequestParam int year, @RequestParam int month) {
        RetrievePeriodExerciseRecordsResponse response = exerciseRecordService.retrievePeriodExerciseRecords(
            userContext, MonthlyDatePeriod.of(year, month));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{recordId}/snapshot")
    public ResponseEntity<CreatedResponse> createSnapshot(@Login UserContext userContext, @PathVariable long recordId) {
        long createdId = exerciseRecordService.createSnapshot(userContext, recordId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreatedResponse.of(createdId));
    }

    @DeleteMapping("/snapshot/{snapshotId}")
    public ResponseEntity<CreatedResponse> deleteSnapshot(@Login UserContext userContext,
        @PathVariable long snapshotId) {
        exerciseRecordService.deleteSnapshot(userContext, snapshotId);
        return ResponseEntity.noContent().build();
    }
}
