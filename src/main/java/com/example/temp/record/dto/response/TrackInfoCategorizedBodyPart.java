package com.example.temp.record.dto.response;

import com.example.temp.machine.domain.BodyPart;
import com.example.temp.record.domain.SetInTrack;
import com.example.temp.record.domain.Track;
import java.util.List;

public record TrackInfoCategorizedBodyPart(
    String bodyPart,
    List<TrackSummary> tracks
) {

    public static TrackInfoCategorizedBodyPart of(BodyPart bodyPart, List<Track> tracks) {
        List<TrackSummary> trackSummaries = tracks.stream()
            .map(TrackSummary::from)
            .toList();
        return new TrackInfoCategorizedBodyPart(bodyPart.getText(), trackSummaries);
    }

    public record TrackSummary(
        String machineName,
        int weight,
        int repeat,
        int set
    ) {

        public static TrackSummary from(Track track) {
            SetInTrack firstSet = track.getSetsInTrack().get(0);
            return new TrackSummary(track.getMachineName(),
                firstSet.getWeight(),
                firstSet.getRepeatCnt(),
                track.getSetsInTrack().size());
        }
    }
}
