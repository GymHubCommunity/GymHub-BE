package com.example.temp.record.dto.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.SetInTrack;
import com.example.temp.record.domain.Track;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest.TrackCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest.TrackCreateRequest.SetInTrackCreateRequest;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExerciseRecordCreateRequestTest {

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

    @Test
    @DisplayName("TrackCreateRequest의 toEntity 메서드로 Track을 생성한다.")
    void createTrack() throws Exception {
        // given
        SetInTrackCreateRequest setInTrackCreateRequest = new SetInTrackCreateRequest(10, 1);
        TrackCreateRequest request = new TrackCreateRequest("머신이름", List.of(setInTrackCreateRequest));

        // when
        Track track = request.toEntityUsing(new HashMap<>());

        // then
        assertThat(track.getMachineName()).isEqualTo(request.machineName());
        assertThat(track.getSetsInTrack()).hasSize(1)
            .extracting("order", "weight", "repeatCnt")
            .contains(
                tuple(1, setInTrackCreateRequest.weight(), setInTrackCreateRequest.repeatCnt()));
    }


    @Test
    @DisplayName("ExerciseRecordCreateRequest의 toEntityWith 메서드로 ExerciseRecord를 생성한다.")
    void createExerciseRecord() throws Exception {
        // given
        TrackCreateRequest trackCreateRequest = makeTrackCreateRequest("머신이름");

        ExerciseRecordCreateRequest request = new ExerciseRecordCreateRequest(List.of(trackCreateRequest));

        // when
        ExerciseRecord exerciseRecord = request.toEntityWith(null, new HashMap<>());

        // then
        assertThat(exerciseRecord.getTracks()).hasSize(1)
            .extracting("machineName")
            .containsExactlyInAnyOrder("머신이름");
    }

    private static TrackCreateRequest makeTrackCreateRequest(String machineName) {
        SetInTrackCreateRequest setInTrackCreateRequest = new SetInTrackCreateRequest(10, 1);
        TrackCreateRequest trackCreateRequest = new TrackCreateRequest(machineName, List.of(setInTrackCreateRequest));
        return trackCreateRequest;
    }
}