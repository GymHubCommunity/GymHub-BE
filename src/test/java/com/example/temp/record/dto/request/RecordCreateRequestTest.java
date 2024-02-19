package com.example.temp.record.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.record.domain.SetInTrack;
import com.example.temp.record.dto.request.RecordCreateRequest.TrackCreateRequest.SetInTrackCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecordCreateRequestTest {

    @Test
    @DisplayName("SetCreateRequest의 toEntityWithOrder 메서드로 SetInTrack을 생성한다.")
    void createSetInTrack() throws Exception {
        // given
        int weight = 30;
        int repeatCnt = 10;
        SetInTrackCreateRequest request = new SetInTrackCreateRequest(weight, repeatCnt);

        // when
        SetInTrack setInTrack = request.toEntityWithOrder(1);

        // then
        assertThat(setInTrack.getOrder()).isEqualTo(1);
        assertThat(setInTrack.getWeight()).isEqualTo(weight);
        assertThat(setInTrack.getRepeatCnt()).isEqualTo(repeatCnt);
    }

}