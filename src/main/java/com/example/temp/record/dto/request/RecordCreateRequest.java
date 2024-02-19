package com.example.temp.record.dto.request;

import com.example.temp.member.domain.Member;
import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.SetInTrack;
import com.example.temp.record.domain.Track;
import java.util.ArrayList;
import java.util.List;

public record RecordCreateRequest(
    List<TrackCreateRequest> tracks
) {

    public ExerciseRecord toEntityWith(Member member) {
        List<Track> tracks = this.tracks.stream()
            .map(TrackCreateRequest::toEntity)
            .toList();
        return ExerciseRecord.create(member, tracks);
    }

    public record TrackCreateRequest(
        String machineName,
        List<SetCreateRequest> sets
    ) {

        public Track toEntity() {
            int order = 1;
            List<SetInTrack> setInTracks = new ArrayList<>();
            for (SetCreateRequest set : this.sets) {
                setInTracks.add(set.toEntityWithOrder(order++));
            }
            return Track.createWithoutRecord(machineName, setInTracks);
        }

        public record SetCreateRequest(
            int weight,
            int repeatCnt
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
