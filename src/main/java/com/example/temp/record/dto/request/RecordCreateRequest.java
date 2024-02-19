package com.example.temp.record.dto.request;

import java.util.List;

public record RecordCreateRequest(
    List<TrackCreateRequest> tracks
) {

    public record TrackCreateRequest(
        String machineName,
        List<SetCreateRequest> sets
    ) {

        public record SetCreateRequest(
            int weight,
            int repeatCnt
        ) {

        }
    }
}
