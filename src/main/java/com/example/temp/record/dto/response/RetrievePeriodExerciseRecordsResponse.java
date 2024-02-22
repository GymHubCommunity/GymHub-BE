package com.example.temp.record.dto.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record RetrievePeriodExerciseRecordsResponse(
    List<RetrievePeriodRecordsElement> results
) {

    public static RetrievePeriodExerciseRecordsResponse from(Map<LocalDate, List<ExerciseRecordInfo>> result) {
        List<RetrievePeriodRecordsElement> results = new ArrayList<>();
        for (Entry<LocalDate, List<ExerciseRecordInfo>> entry : result.entrySet()) {
            results.add(RetrievePeriodRecordsElement.of(entry.getKey(), entry.getValue()));
        }
        return new RetrievePeriodExerciseRecordsResponse(results);
    }

    public record RetrievePeriodRecordsElement(
        String date,
        List<ExerciseRecordInfo> exerciseRecords
    ) {

        public static RetrievePeriodRecordsElement of(LocalDate localDate, List<ExerciseRecordInfo> exerciseRecords) {
            return new RetrievePeriodRecordsElement(localDate.toString(), exerciseRecords);
        }
    }
}
