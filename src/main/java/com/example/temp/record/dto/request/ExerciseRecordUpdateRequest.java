package com.example.temp.record.dto.request;

import com.example.temp.machine.domain.BodyPart;
import com.example.temp.member.domain.Member;
import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.SetInTrack;
import com.example.temp.record.domain.Track;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

public record ExerciseRecordUpdateRequest(

    @NotNull
    List<@Valid TrackUpdateRequest> tracks
) {

    public ExerciseRecord toEntityWith(Member member) {
        List<Track> tracks = this.tracks.stream()
            .map(TrackUpdateRequest::toEntity)
            .toList();
        return ExerciseRecord.create(member, tracks);
    }

    public record TrackUpdateRequest(

        @NotBlank
        String machineName,

        @NotNull
        List<@Valid SetInTrackUpdateRequest> sets
    ) {

        public Track toEntity() {
            int order = 1;
            List<SetInTrack> setInTracks = new ArrayList<>();
            for (SetInTrackUpdateRequest set : this.sets) {
                setInTracks.add(set.toEntityWithOrder(order++));
            }
            return Track.createWithoutRecord(machineName, BodyPart.CARDIO, setInTracks); // TODO
        }

        public record SetInTrackUpdateRequest(
            @PositiveOrZero
            Integer weight,

            @PositiveOrZero
            Integer repeatCnt
        ) {

            public SetInTrack toEntityWithOrder(int order) {
                return SetInTrack.builder()
                    .weight(weight())
                    .repeatCnt(repeatCnt())
                    .order(order)
                    .build();
            }
        }
    }
}
