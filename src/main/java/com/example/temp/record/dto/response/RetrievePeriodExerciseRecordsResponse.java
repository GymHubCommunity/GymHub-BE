package com.example.temp.record.dto.response;

import com.example.temp.record.domain.ExerciseRecord;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record RetrievePeriodExerciseRecordsResponse(
    List<RetrievePeriodRecordsElement> results
) {

    public static RetrievePeriodExerciseRecordsResponse from(Map<LocalDate, List<ExerciseRecord>> result) {
        List<RetrievePeriodRecordsElement> results = new ArrayList<>();
        for (Entry<LocalDate, List<ExerciseRecord>> entry : result.entrySet()) {
            results.add(RetrievePeriodRecordsElement.of(entry.getKey(), entry.getValue()));
        }
        return new RetrievePeriodExerciseRecordsResponse(results);
    }

    public record RetrievePeriodRecordsElement(
        String id,
        List<ExerciseRecordInfo> exerciseRecords
    ) {

        // TODO Formatter 입력받도록 구현
        public static RetrievePeriodRecordsElement of(LocalDate localDate, List<ExerciseRecord> exerciseRecords) {
            List<ExerciseRecordInfo> exerciseRecordInfos = exerciseRecords.stream()
                .map(ExerciseRecordInfo::from)
                .toList();
            return new RetrievePeriodRecordsElement(localDate.toString(), exerciseRecordInfos);
        }
    }
}
