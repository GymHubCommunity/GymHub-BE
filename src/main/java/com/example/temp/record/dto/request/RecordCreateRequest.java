package com.example.temp.record.dto.request;

import java.util.List;

public record RecordCreateRequest(
    List<RecordsTrackCreateRequest> tracks
) {

    public record RecordsTrackCreateRequest(
        String machineName,
        List<TracksSetCreateRequest> sets
    ) {

        public record TracksSetCreateRequest(
            int setNum,
            int weight,
            int repeatCnt
        ) {

        }
    }
}
