package com.example.temp.record.dto.response;

import com.example.temp.record.domain.ExerciseRecord;
import java.util.List;
import org.springframework.data.domain.Slice;

public record RecordSnapshotsResponse(
    List<ExerciseRecordInfo> snapshots,
    boolean hasNext
) {

    public static RecordSnapshotsResponse create(Slice<ExerciseRecord> snapshots) {
        List<ExerciseRecordInfo> exerciseRecordInfos = snapshots.stream()
            .map(ExerciseRecordInfo::from)
            .toList();
        return new RecordSnapshotsResponse(exerciseRecordInfos, snapshots.hasNext());
    }
}
