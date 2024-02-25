package com.example.temp.record.dto.response;

import com.example.temp.machine.domain.BodyPart;
import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.Track;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record ExerciseRecordResponse(
    Long recordId,
    LocalDate recordDate,
    List<TrackInfoCategorizedBodyPart> tracksCategorizedBodyPart
) {

    public static ExerciseRecordResponse from(ExerciseRecord exerciseRecord) {
        Map<BodyPart, List<Track>> map = new EnumMap<>(BodyPart.class);
        for (Track track : exerciseRecord.getTracks()) {
            map.putIfAbsent(track.getMajorBodyPart(), new ArrayList<>());
            map.get(track.getMajorBodyPart()).add(track);
        }

        List<TrackInfoCategorizedBodyPart> results = new ArrayList<>();
        for (Entry<BodyPart, List<Track>> entry : map.entrySet()) {
            results.add(TrackInfoCategorizedBodyPart.of(entry.getKey(), entry.getValue()));
        }
        return new ExerciseRecordResponse(exerciseRecord.getId(), exerciseRecord.getRecordDate(), results);
    }
}
