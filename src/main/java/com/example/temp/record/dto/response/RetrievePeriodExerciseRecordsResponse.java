package com.example.temp.record.dto.response;

import com.example.temp.record.domain.ExerciseRecord;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record RetrievePeriodExerciseRecordsResponse(
    List<RetrievePeriodRecordsElement> results
) {

    public static RetrievePeriodExerciseRecordsResponse of(int year, int month, List<ExerciseRecord> records) {
        // 연도와 월을 통해 날짜를 가져온다.
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate last = start.withDayOfMonth(start.lengthOfMonth());
        LocalDate cursor = start;
        Map<String, List<ExerciseRecord>> hash = new HashMap<>();
        while (!cursor.isAfter(last)) {
            hash.put(cursor.toString(), new ArrayList<>());
            cursor = cursor.plusDays(1L);
        }
        for (ExerciseRecord record : records) {
            if (!hash.containsKey(record.getRecordDate().toString())) {
                throw new IllegalArgumentException("year와 Month가 일치하지 않는 운동 결과가 발생했습니다.");
            }
            hash.get(record.getRecordDate().toString()).add(record);
        }

        List<RetrievePeriodRecordsElement> results = new ArrayList<>();
        for (Entry<String, List<ExerciseRecord>> entry : hash.entrySet()) {
            results.add(RetrievePeriodRecordsElement.of(entry.getKey(), entry.getValue()));
        }
        return new RetrievePeriodExerciseRecordsResponse(results);
    }

    public record RetrievePeriodRecordsElement(
        String id,
        List<ExerciseRecordInfo> exerciseRecords
    ) {

        public static RetrievePeriodRecordsElement of(String id, List<ExerciseRecord> exerciseRecords) {
            List<ExerciseRecordInfo> exerciseRecordInfos = exerciseRecords.stream()
                .map(ExerciseRecordInfo::from)
                .toList();
            return new RetrievePeriodRecordsElement(id, exerciseRecordInfos);
        }
    }
}
