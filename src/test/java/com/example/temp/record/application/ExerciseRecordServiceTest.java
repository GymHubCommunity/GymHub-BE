package com.example.temp.record.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.example.temp.auth.domain.Role;
import com.example.temp.common.domain.period.MonthlyDatePeriod;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.entity.Email;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.machine.domain.BodyPart;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.SetInTrack;
import com.example.temp.record.domain.Track;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest.TrackCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest.TrackCreateRequest.SetInTrackCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest;
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest.TrackUpdateRequest;
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest.TrackUpdateRequest.SetInTrackUpdateRequest;
import com.example.temp.record.dto.response.ExerciseRecordResponse;
import com.example.temp.record.dto.response.RetrievePeriodExerciseRecordsResponse;
import com.example.temp.record.dto.response.RetrievePeriodExerciseRecordsResponse.RetrievePeriodRecordsElement;
import com.example.temp.record.dto.response.RetrieveRecordSnapshotsResponse;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ExerciseRecordServiceTest {

    @Autowired
    ExerciseRecordService exerciseRecordService;

    @Autowired
    EntityManager em;

    Member loginMember;

    UserContext loginUserContext;

    UserContext noLoginUserContext;

    @BeforeEach
    void setUp() {
        loginMember = saveMember("loginMember");
        loginUserContext = UserContext.fromMember(loginMember);
        noLoginUserContext = new UserContext(99999999L, Role.NORMAL);
    }

    @Test
    @DisplayName("운동 기록을 저장한다.")
    void create() throws Exception {
        // given
        ExerciseRecordCreateRequest request = makeExerciseRecordCreateRequest("머신1", 10, 3);

        // when
        long createdId = exerciseRecordService.create(loginUserContext, request);

        // then
        ExerciseRecord exerciseRecord = em.find(ExerciseRecord.class, createdId);
        assertThat(exerciseRecord.getRecordDate()).isNotNull();
        assertThat(exerciseRecord.getMember()).isEqualTo(loginMember);
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 운동 기록을 할 수 없다.")
    void createFailNoAuthenticated() throws Exception {
        // given
        ExerciseRecordCreateRequest request = new ExerciseRecordCreateRequest(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.create(noLoginUserContext, request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("운동기록을 저장하면 연관된 세트들이 저장된다.")
    void createRecordSuccess() throws Exception {
        // given
        SetInTrackCreateRequest set1CreateRequest = new SetInTrackCreateRequest(10, 3);
        SetInTrackCreateRequest set2CreateRequest = new SetInTrackCreateRequest(20, 5);
        TrackCreateRequest trackCreateRequest = new TrackCreateRequest("스쿼트",
            List.of(set1CreateRequest, set2CreateRequest));
        ExerciseRecordCreateRequest request = new ExerciseRecordCreateRequest(List.of(trackCreateRequest));

        // when
        long createdId = exerciseRecordService.create(loginUserContext, request);

        // then
        ExerciseRecord exerciseRecord = em.find(ExerciseRecord.class, createdId);
        List<Track> tracks = exerciseRecord.getTracks();
        assertThat(tracks).hasSize(1)
            .extracting("machineName")
            .contains("스쿼트");

        long trackId = tracks.get(0).getId();
        Track track = em.find(Track.class, trackId);
        assertThat(track.getSetsInTrack()).hasSize(2)
            .extracting("order", "weight", "repeatCnt")
            .containsExactlyInAnyOrder(
                tuple(1, set1CreateRequest.weight(), set1CreateRequest.repeatCnt()),
                tuple(2, set2CreateRequest.weight(), set2CreateRequest.repeatCnt())
            );

        assertThat(em.find(SetInTrack.class, track.getSetsInTrack().get(0).getId())).isNotNull();
        assertThat(em.find(SetInTrack.class, track.getSetsInTrack().get(1).getId())).isNotNull();
    }

    @Test
    @DisplayName("운동기록을 삭제한다.")
    void delete() throws Exception {
        // given
        Member member = saveMember("nick1");
        ExerciseRecord record = saveExerciseRecord(member);

        // when
        exerciseRecordService.delete(UserContext.fromMember(member), record.getId());

        // then
        assertThat(em.find(ExerciseRecord.class, record.getId())).isNull();
    }

    /**
     * ExerciseRecord, Track, SetInTrack 엔티티들이 DB에 저장되었는지 확인합니다. delete 명령 이후, 해당 엔티티들이 DB에서 삭제되었는지 확인합니다.
     */
    @Test
    @DisplayName("운동기록이 삭제되면 해당 기록에 포함된 Track, Set 엔티티를 함께 삭제한다.")
    void deleteCheckAllChildrenDelete() throws Exception {
        // given
        Member member = saveMember("nick1");
        Track trackBeforeSaved = createTrack("머신1", List.of(createSetInTrack(1), createSetInTrack(2)));
        ExerciseRecord savedExerciseRecord = saveExerciseRecord(member, trackBeforeSaved);
        Track savedTrack = savedExerciseRecord.getTracks().get(0);
        List<SetInTrack> savedSetsInTrack = savedTrack.getSetsInTrack();

        assertThat(em.find(ExerciseRecord.class, savedExerciseRecord.getId())).isNotNull();
        assertThat(em.find(Track.class, savedTrack.getId())).isNotNull();
        for (SetInTrack setInTrack : savedSetsInTrack) {
            assertThat(em.find(SetInTrack.class, setInTrack.getId())).isNotNull();
        }

        // when
        exerciseRecordService.delete(UserContext.fromMember(member), savedExerciseRecord.getId());

        // then
        assertThat(em.find(ExerciseRecord.class, savedExerciseRecord.getId())).isNull();
        assertThat(em.find(Track.class, savedTrack.getId())).isNull();
        for (SetInTrack setInTrack : savedSetsInTrack) {
            assertThat(em.find(SetInTrack.class, setInTrack.getId())).isNull();
        }
    }

    @Test
    @DisplayName("기간별 운동기록을 조회한다.")
    void retrievePeriodExerciseRecords() throws Exception {
        // given
        int year = 2024;
        int month = 1;
        LocalDate recordDate = LocalDate.of(year, month, 1);
        saveExerciseRecord(loginMember, recordDate);

        // when
        RetrievePeriodExerciseRecordsResponse response =
            exerciseRecordService.retrievePeriodExerciseRecords(loginUserContext, MonthlyDatePeriod.of(year, month));

        // then
        int totalCntInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
        assertThat(response.results()).hasSize(totalCntInMonth);
        RetrievePeriodRecordsElement periodRecordsElement = response.results().stream()
            .filter(each -> each.date().equals(recordDate.toString()))
            .findAny()
            .get();
        assertThat(periodRecordsElement.exerciseRecords()).isNotEmpty();
    }

    @Test
    @DisplayName("기간별 운동 기록을 조회할 때, 해당 기간에 포함되지 않으면 조회되지 않는다")
    void retrievePeriodExerciseRecordsOutOfRange() throws Exception {
        // given
        int year = 2024;
        int month = 1;
        saveExerciseRecord(loginMember, LocalDate.of(year, month + 1, 1));

        // when
        RetrievePeriodExerciseRecordsResponse response =
            exerciseRecordService.retrievePeriodExerciseRecords(loginUserContext, MonthlyDatePeriod.of(year, month));
        // then
        int totalCntInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
        assertThat(response.results()).hasSize(totalCntInMonth);
        RetrievePeriodRecordsElement periodRecordsElement = response.results().stream()
            .filter(each -> each.date().equals(LocalDate.of(year, month, 1).toString()))
            .findAny()
            .get();
        assertThat(periodRecordsElement.exerciseRecords()).isEmpty();
    }

    @Test
    @DisplayName("기간별 운동기록을 조회하면 내가 등록한 기록만 볼 수 있다.")
    void retrievePeriodExerciseRecordsOnlyMyRecord() throws Exception {
        // given
        int year = 2024;
        int month = 1;
        LocalDate recordDate = LocalDate.of(year, month, 1);
        Member anotherMember = saveMember("nick2");
        saveExerciseRecord(anotherMember, recordDate);

        // when
        RetrievePeriodExerciseRecordsResponse response =
            exerciseRecordService.retrievePeriodExerciseRecords(loginUserContext, MonthlyDatePeriod.of(year, month));

        // then
        int totalCntInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
        assertThat(response.results()).hasSize(totalCntInMonth);
        RetrievePeriodRecordsElement periodRecordsElement = response.results().stream()
            .filter(each -> each.date().equals(recordDate.toString()))
            .findAny()
            .get();
        assertThat(periodRecordsElement.exerciseRecords()).isEmpty();
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 월별 운동기록을 조회할 수 없다.")
    void retrievePeriodExerciseRecordsFailNoAuthN() throws Exception {
        // given
        int year = 2024;
        int month = 1;
        saveExerciseRecord(loginMember, LocalDate.of(year, month, 1));

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.retrievePeriodExerciseRecords(noLoginUserContext,
            MonthlyDatePeriod.of(year, month)))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("운동기록의 스냅샷을 생성한다.")
    void createSnapshot() throws Exception {
        // given
        Member member = saveMember("nick");
        ExerciseRecord original = saveExerciseRecord(member);
        assertThat(original.isSnapshot()).isFalse();

        // when
        long createdId = exerciseRecordService.createSnapshot(UserContext.fromMember(member), original.getId());

        // then
        em.flush();
        em.clear();

        ExerciseRecord copy = em.find(ExerciseRecord.class, createdId);

        assertThat(copy.getId()).isNotEqualTo(original.getId());
        assertThat(copy.isSnapshot()).isTrue();

        assertThat(copy.getTracks()).hasSize(original.getTracks().size());
        List<Track> tracksInOriginal = original.getTracks();
        List<Track> tracksInCopy = copy.getTracks();
        validateAllTracksIdIsDifferent(tracksInCopy, tracksInOriginal);
        validateAllTrackMachineNameIsSame(tracksInCopy, tracksInOriginal);
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 운동기록 스냅샷을 만들 수 없다.")
    void createSnapshotFailExerciseRecordNoAuthN() throws Exception {
        // given
        ExerciseRecord original = saveExerciseRecord(loginMember);

        // when & then
        assertThatThrownBy(
            () -> exerciseRecordService.createSnapshot(noLoginUserContext, original.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("자신이 작성하지 않은 운동기록을 사용해서 스냅샷을 만들 수 없다.")
    void createSnapshotFailExerciseRecordNoAuthZ() throws Exception {
        // given
        Member member = saveMember("nick");
        ExerciseRecord original = saveExerciseRecord(member);

        // when & then
        assertThatThrownBy(
            () -> exerciseRecordService.createSnapshot(loginUserContext, original.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHORIZED_FAIL.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 운동기록의 스냅샷은 생성할 수 없다.")
    void createSnapshotFailExerciseRecordNotFound() throws Exception {
        // given
        Member member = saveMember("nick");
        long notExistExerciseRecordId = 999_999_999L;

        // when & then
        assertThatThrownBy(
            () -> exerciseRecordService.createSnapshot(UserContext.fromMember(member), notExistExerciseRecordId))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.RECORD_NOT_FOUND.getMessage());
    }

    private void validateAllTrackMachineNameIsSame(List<Track> tracksInCopy, List<Track> tracksInOriginal) {
        Set<String> machineNamesInCopy = tracksInCopy.stream().map(Track::getMachineName).collect(Collectors.toSet());
        Set<String> trackNamesInOriginal = tracksInOriginal.stream().map(Track::getMachineName)
            .collect(Collectors.toSet());
        for (String trackNameInCopy : machineNamesInCopy) {
            assertThat(trackNamesInOriginal).contains(trackNameInCopy);
        }
    }

    private void validateAllTracksIdIsDifferent(List<Track> tracksInCopy, List<Track> tracksInOriginal) {
        Set<Long> trackIdsInCopy = tracksInCopy.stream().map(Track::getId).collect(Collectors.toSet());
        Set<Long> trackIdsInOriginal = tracksInOriginal.stream().map(Track::getId).collect(Collectors.toSet());
        for (Long trackIdInCopy : trackIdsInCopy) {
            assertThat(trackIdsInOriginal).doesNotContain(trackIdInCopy);
        }
    }

    private Track createTrack(String machineName, List<SetInTrack> setsInTrack) {
        return Track.builder()
            .machineName(machineName)
            .setsInTrack(setsInTrack)
            .majorBodyPart(BodyPart.CARDIO)
            .build();
    }

    private SetInTrack createSetInTrack(int order) {
        return SetInTrack.builder()
            .order(order)
            .weight(10)
            .repeatCnt(5)
            .build();
    }

    @Test
    @DisplayName("로그인한 사용자만 운동기록을 삭제할 수 있다.")
    void deleteFailNoAuthN() throws Exception {
        // given
        ExerciseRecord record = saveExerciseRecord(loginMember);

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.delete(noLoginUserContext, record.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("인가 권한이 없는 사용자는 운동기록을 삭제할 수 없다.")
    void deleteFailNoAuthZ() throws Exception {
        // given
        ExerciseRecord record = saveExerciseRecord(loginMember);
        Member anotherMember = saveMember("another1");

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.delete(UserContext.fromMember(anotherMember), record.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHORIZED_FAIL.getMessage());
    }

    @Test
    @DisplayName("운동기록 수정 요청을 보내면, 기존에 등록되어 있던 트랙들은 삭제되고 새로운 트랙들로 대체된다.")
    void updateSuccess() throws Exception {
        // given
        Track trackBeforeSaved = createTrack("머신1", List.of(createSetInTrack(1), createSetInTrack(2)));
        ExerciseRecord record = saveExerciseRecord(loginMember, trackBeforeSaved);
        ExerciseRecordUpdateRequest request = makeExerciseRecordUpdateRequest("변경한_머신", 100, 10);

        Long prevTrackId = record.getTracks().get(0).getId();
        assertThat(em.find(Track.class, prevTrackId)).isNotNull();

        // when
        exerciseRecordService.update(loginUserContext, record.getId(), request);
        em.flush();
        em.clear();

        // then
        ExerciseRecord updatedRecord = em.find(ExerciseRecord.class, record.getId());

        assertThat(updatedRecord.getRecordDate()).isEqualTo(record.getRecordDate());
        assertThat(updatedRecord.getTracks()).hasSize(request.tracks().size());
        assertThat(em.find(Track.class, prevTrackId)).isNull();
    }

    @Test
    @DisplayName("로그인한 사용자만 운동기록을 수정할 수 있다.")
    void updateFailNoAuthN() throws Exception {
        // given
        ExerciseRecord record = saveExerciseRecord(loginMember);
        ExerciseRecordUpdateRequest request = new ExerciseRecordUpdateRequest(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.update(noLoginUserContext, record.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("인가 권한이 없는 사용자는 운동기록을 수정할 수 없다.")
    void updateFailNoAuthZ() throws Exception {
        // given
        ExerciseRecord record = saveExerciseRecord(loginMember);
        Member anotherMember = saveMember("another1");
        ExerciseRecordUpdateRequest request = new ExerciseRecordUpdateRequest(Collections.emptyList());

        // when & then
        assertThatThrownBy(() ->
            exerciseRecordService.update(UserContext.fromMember(anotherMember), record.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHORIZED_FAIL.getMessage());
    }

    @Test
    @DisplayName("스냅샷을 삭제한다.")
    void deleteSnapshot() throws Exception {
        // given
        Member member = saveMember("nick1");
        ExerciseRecord record = saveSnapshot(member);

        // when
        exerciseRecordService.deleteSnapshot(UserContext.fromMember(member), record.getId());

        // then
        assertThat(em.find(ExerciseRecord.class, record.getId())).isNull();
    }

    /**
     * ExerciseRecord(스냅샷), Track, SetInTrack 엔티티들이 DB에 저장되었는지 확인합니다. deleteSnapshot 명령 이후, 해당 엔티티들이 DB에서 삭제되었는지 확인합니다.
     */
    @Test
    @DisplayName("운동기록 스냅샷이 삭제되면 해당 기록에 포함된 Track, Set 엔티티를 함께 삭제한다.")
    void deleteSnapshotCheckAllChildrenDelete() throws Exception {
        // given
        Member member = saveMember("nick1");
        Track trackBeforeSaved = createTrack("머신1", List.of(createSetInTrack(1), createSetInTrack(2)));
        ExerciseRecord snapshot = saveSnapshot(member, trackBeforeSaved);
        Track savedTrack = snapshot.getTracks().get(0);
        List<SetInTrack> savedSetsInTrack = savedTrack.getSetsInTrack();

        assertThat(em.find(ExerciseRecord.class, snapshot.getId())).isNotNull();
        assertThat(em.find(Track.class, savedTrack.getId())).isNotNull();
        for (SetInTrack setInTrack : savedSetsInTrack) {
            assertThat(em.find(SetInTrack.class, setInTrack.getId())).isNotNull();
        }

        // when
        exerciseRecordService.deleteSnapshot(UserContext.fromMember(member), snapshot.getId());

        // then
        assertThat(em.find(ExerciseRecord.class, snapshot.getId())).isNull();
        assertThat(em.find(Track.class, savedTrack.getId())).isNull();
        for (SetInTrack setInTrack : savedSetsInTrack) {
            assertThat(em.find(SetInTrack.class, setInTrack.getId())).isNull();
        }
    }

    @Test
    @DisplayName("로그인한 사용자만 운동기록 스냅샷을 삭제할 수 있다.")
    void deleteSnapshotFailNoAuthN() throws Exception {
        // given
        ExerciseRecord snapshot = saveSnapshot(loginMember);

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.deleteSnapshot(noLoginUserContext, snapshot.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("인가 권한이 없는 사용자는 운동기록 스냅샷을 삭제할 수 없다.")
    void deleteSnapshotFailNoAuthZ() throws Exception {
        // given
        ExerciseRecord snapshot = saveSnapshot(loginMember);
        Member anotherMember = saveMember("another1");

        // when & then
        assertThatThrownBy(
            () -> exerciseRecordService.deleteSnapshot(UserContext.fromMember(anotherMember), snapshot.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHORIZED_FAIL.getMessage());
    }

    @Test
    @DisplayName("운동기록 스냅샷 목록을 불러온다. 만약 다음 데이터가 존재하면 hasNext 필드에 true를 반환한다.")
    void retrieveSnapshotsThatHasNextTrue() throws Exception {
        // given
        ExerciseRecord snapshot1 = saveSnapshot(loginMember);
        ExerciseRecord snapshot2 = saveSnapshot(loginMember);
        ExerciseRecord snapshot3 = saveSnapshot(loginMember);

        // when
        RetrieveRecordSnapshotsResponse response = exerciseRecordService.retrieveSnapshots(loginUserContext, null,
            Pageable.ofSize(2));

        // then
        assertThat(response.hasNext()).isTrue();
        assertThat(response.snapshots()).hasSize(2)
            .extracting(ExerciseRecordResponse::recordId)
            .containsExactly(snapshot3.getId(), snapshot2.getId());
    }

    @Test
    @DisplayName("운동기록 스냅샷 목록을 불러온다. 만약 다음 데이터가 존재하면 hasNext 필드에 false를 반환한다.")
    void retrieveSnapshotsThatHasNextFalse() throws Exception {
        // given
        ExerciseRecord snapshot1 = saveSnapshot(loginMember);
        ExerciseRecord snapshot2 = saveSnapshot(loginMember);

        // when
        RetrieveRecordSnapshotsResponse response = exerciseRecordService.retrieveSnapshots(loginUserContext, null,
            Pageable.ofSize(2));

        // then
        assertThat(response.hasNext()).isFalse();
        assertThat(response.snapshots()).hasSize(2)
            .extracting(ExerciseRecordResponse::recordId)
            .containsExactly(snapshot2.getId(), snapshot1.getId());
    }

    private ExerciseRecord saveExerciseRecord(Member member, LocalDate date) {
        Track tracks = createTrack("머신1", List.of(createSetInTrack(1)));
        return saveExerciseRecordHelper(member, tracks, date, false);
    }

    private ExerciseRecord saveExerciseRecord(Member member) {
        return saveExerciseRecord(member, LocalDate.now());
    }

    private ExerciseRecord saveExerciseRecord(Member member, Track track) {
        return saveExerciseRecordHelper(member, track, LocalDate.now(), false);
    }

    private ExerciseRecord saveSnapshot(Member member) {
        Track track = createTrack("머신1", List.of(createSetInTrack(1)));
        return saveExerciseRecordHelper(member, track, LocalDate.now(), true);
    }

    private ExerciseRecord saveSnapshot(Member member, Track track) {
        return saveExerciseRecordHelper(member, track, LocalDate.now(), true);
    }

    private ExerciseRecord saveExerciseRecordHelper(Member member, Track track, LocalDate date, boolean isSnapshot) {
        ExerciseRecord record = ExerciseRecord.builder()
            .member(member)
            .tracks(List.of(track))
            .recordDate(date)
            .isSnapshot(isSnapshot)
            .build();
        em.persist(record);
        return record;
    }

    private Member saveMember(String nickname) {
        Member member = Member.builder()
            .email(Email.create("test@test.com"))
            .followStrategy(FollowStrategy.EAGER)
            .privacyPolicy(PrivacyPolicy.PUBLIC)
            .profileUrl("https://profileurl")
            .nickname(Nickname.create(nickname))
            .build();
        em.persist(member);
        return member;
    }

    /**
     * machineName이라는 운동 기구를 사용하는 1세트짜리 트랙을 CREATE하는 요청을 만듭니다.
     */
    private ExerciseRecordCreateRequest makeExerciseRecordCreateRequest(String machineName, int weight, int repeatCnt) {
        SetInTrackCreateRequest setInTrackCreateRequest = new SetInTrackCreateRequest(weight, repeatCnt);
        List<TrackCreateRequest> trackCreateRequests = List.of(
            new TrackCreateRequest(machineName, List.of(setInTrackCreateRequest)));
        return new ExerciseRecordCreateRequest(trackCreateRequests);
    }


    /**
     * machineName이라는 운동 기구를 사용하는 1세트짜리 트랙을 UPDATE하는 요청을 만듭니다.
     */
    private ExerciseRecordUpdateRequest makeExerciseRecordUpdateRequest(String machineName, int weight, int repeatCnt) {
        SetInTrackUpdateRequest setInTrackUpdateRequest = new SetInTrackUpdateRequest(weight, repeatCnt);
        List<TrackUpdateRequest> trackUpdateRequests = List.of(
            new TrackUpdateRequest(machineName, List.of(setInTrackUpdateRequest)));
        return new ExerciseRecordUpdateRequest(trackUpdateRequests);
    }


}