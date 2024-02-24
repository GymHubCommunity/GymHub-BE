package com.example.temp.record.application;

import com.example.temp.common.domain.period.DatePeriod;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.domain.Machine;
import com.example.temp.machine.domain.MachineRepository;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.ExerciseRecordRepository;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest.TrackCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest;
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest.TrackUpdateRequest;
import com.example.temp.record.dto.response.ExerciseRecordInfo;
import com.example.temp.record.dto.response.RetrievePeriodExerciseRecordsResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MemberRepository memberRepository;
    private final MachineRepository machineRepository;

    @Transactional
    public long create(UserContext userContext, ExerciseRecordCreateRequest request) {
        Member member = findMember(userContext);
        List<String> machineNames = request.tracks().stream()
            .map(TrackCreateRequest::machineName)
            .toList();
        Map<String, BodyPart> machineToMajorBodyPartMap = createMachineToMajorBodyPartMap(machineNames);
        ExerciseRecord exerciseRecord = request.toEntityWith(member, machineToMajorBodyPartMap);
        exerciseRecordRepository.save(exerciseRecord);
        return exerciseRecord.getId();
    }

    private Map<String, BodyPart> createMachineToMajorBodyPartMap(List<String> machineNames) {
        Map<String, BodyPart> machineToMajorBodyPartMap = machineRepository.findAllByNameIn(machineNames).stream()
            .collect(Collectors.toMap(Machine::getName, Machine::getMajorBodyPart));
        machineNames.forEach(name -> machineToMajorBodyPartMap.putIfAbsent(name, BodyPart.ETC));
        return machineToMajorBodyPartMap;
    }

    public RetrievePeriodExerciseRecordsResponse retrievePeriodExerciseRecords(UserContext userContext,
        DatePeriod datePeriod) {
        Member member = findMember(userContext);
        List<ExerciseRecordInfo> exerciseRecords = exerciseRecordRepository
            .findAllByMemberAndPeriod(member, datePeriod.getStartDate(), datePeriod.getLastDate()).stream()
            .map(ExerciseRecordInfo::from)
            .toList();

        Map<LocalDate, List<ExerciseRecordInfo>> exerciseRecordMap = convertListToMapByDate(datePeriod,
            exerciseRecords);
        return RetrievePeriodExerciseRecordsResponse.from(exerciseRecordMap);
    }

    private Map<LocalDate, List<ExerciseRecordInfo>> convertListToMapByDate(DatePeriod datePeriod,
        List<ExerciseRecordInfo> exerciseRecords) {
        Map<LocalDate, List<ExerciseRecordInfo>> result = createInitStatusMapByDate(datePeriod);
        for (ExerciseRecordInfo exerciseRecord : exerciseRecords) {
            if (!result.containsKey(exerciseRecord.recordDate())) {
                throw new IllegalArgumentException("year와 month가 일치하지 않는 운동 결과가 발생했습니다.");
            }
            result.get(exerciseRecord.recordDate()).add(exerciseRecord);
        }
        return result;
    }

    private Map<LocalDate, List<ExerciseRecordInfo>> createInitStatusMapByDate(DatePeriod datePeriod) {
        Map<LocalDate, List<ExerciseRecordInfo>> result = new HashMap<>();
        LocalDate cursor = datePeriod.getStartDate();
        while (!cursor.isAfter(datePeriod.getLastDate())) {
            result.put(cursor, new ArrayList<>());
            cursor = cursor.plusDays(1L);
        }
        return result;
    }

    /**
     * 기존 Track과 Set을 모두 지우고 새로운 데이터를 넣는 방식을 사용할 예정입니다. 추후 내부 로직을 리팩토링할 예정입니다.
     */
    @Transactional
    public void update(UserContext userContext, long targetId, ExerciseRecordUpdateRequest request) {
        Member member = findMember(userContext);
        List<String> machineNames = request.tracks().stream()
            .map(TrackUpdateRequest::machineName)
            .toList();
        Map<String, BodyPart> machineToMajorBodyPartMap = createMachineToMajorBodyPartMap(machineNames);

        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(ErrorCode.RECORD_NOT_FOUND));
        if (!exerciseRecord.isOwnedBy(member)) {
            throw new ApiException(ErrorCode.AUTHORIZED_FAIL);
        }
        exerciseRecord.update(request.toEntityWith(member, machineToMajorBodyPartMap));
    }

    @Transactional
    public void delete(UserContext userContext, long targetId) {
        Member member = findMember(userContext);
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(ErrorCode.RECORD_NOT_FOUND));
        if (!exerciseRecord.isOwnedBy(member)) {
            throw new ApiException(ErrorCode.AUTHORIZED_FAIL);
        }
        exerciseRecordRepository.delete(exerciseRecord);
    }

    @Transactional
    public void createSnapshot(UserContext userContext, long targetId) {
        Member member = findMember(userContext);
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(ErrorCode.RECORD_NOT_FOUND));
        if (!exerciseRecord.isOwnedBy(member)) {
            throw new ApiException(ErrorCode.AUTHORIZED_FAIL);
        }
        ExerciseRecord snapshot = exerciseRecord.createSnapshot(member);
        exerciseRecordRepository.save(snapshot);
    }

    private Member findMember(UserContext userContext) {
        return memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(ErrorCode.AUTHENTICATED_FAIL));
    }
}
