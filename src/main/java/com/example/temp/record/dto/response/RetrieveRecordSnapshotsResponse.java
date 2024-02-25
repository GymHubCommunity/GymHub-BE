package com.example.temp.record.dto.response;

import com.example.temp.record.domain.ExerciseRecord;
import java.util.List;
import org.springframework.data.domain.Slice;

public record RetrieveRecordSnapshotsResponse(
    List<ExerciseRecordResponse> snapshots,
    boolean hasNext
) {

    public static RetrieveRecordSnapshotsResponse from(Slice<ExerciseRecord> snapshots) {
        List<ExerciseRecordResponse> exerciseRecordResponses = snapshots.stream()
            .map(ExerciseRecordResponse::from)
            .toList();
        return new RetrieveRecordSnapshotsResponse(exerciseRecordResponses, snapshots.hasNext());
    }
}
