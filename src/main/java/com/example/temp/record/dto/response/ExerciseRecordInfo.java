package com.example.temp.record.dto.response;

import com.example.temp.record.domain.ExerciseRecord;

public record ExerciseRecordInfo() {

    public static ExerciseRecordInfo from(ExerciseRecord exerciseRecord) {
        // TODO
        return new ExerciseRecordInfo();
    }
}
