package com.example.temp.record.dto.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record RetrievePeriodExerciseRecordsResponse(
    List<RetrievePeriodRecordsElement> results
) {

    public static RetrievePeriodExerciseRecordsResponse of(int year, int month) {
        // 연도와 월을 통해 날짜를 가져온다.
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate last = start.withDayOfMonth(start.lengthOfMonth());
        LocalDate cursor = start;
        List<RetrievePeriodRecordsElement> results = new ArrayList<>();
        while (!cursor.isAfter(last)) {
            results.add(new RetrievePeriodRecordsElement());
            cursor = cursor.plusDays(1L);
        }
        return new RetrievePeriodExerciseRecordsResponse(results);
    }

    public record RetrievePeriodRecordsElement(
//        String id,
//        List<ExerciseRecordInfo> exerciseRecords
    ) {

    }
}
