package com.example.temp.record.dto.response;

import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.SetInTrack;
import com.example.temp.record.domain.Track;
import java.time.LocalDate;
import java.util.List;

public record ExerciseRecordInfo(
    Long recordId,
    LocalDate recordDate,
    List<TrackInfo> tracks
) {

    public static ExerciseRecordInfo from(ExerciseRecord exerciseRecord) {
        List<TrackInfo> tracks = exerciseRecord.getTracks().stream()
            .map(TrackInfo::from)
            .toList();
        return new ExerciseRecordInfo(exerciseRecord.getId(), exerciseRecord.getRecordDate(), tracks);
    }

    public record TrackInfo(
        String machineName,
        String bodyPart,
        int weight,
        int repeat,
        int set
    ) {

        public static TrackInfo from(Track track) {
            SetInTrack firstSet = track.getSetsInTrack().get(0);
            return new TrackInfo(track.getMachineName(),
                track.getMajorBodyPart().getText(),
                firstSet.getWeight(),
                firstSet.getRepeatCnt(),
                track.getSetsInTrack().size());
        }
    }
}
