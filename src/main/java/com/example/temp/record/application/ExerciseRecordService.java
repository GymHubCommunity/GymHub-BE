package com.example.temp.record.application;

import com.example.temp.common.domain.period.DatePeriod;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.ExerciseRecordRepository;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest;
import com.example.temp.record.dto.response.RetrievePeriodExerciseRecordsResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public long create(UserContext userContext, ExerciseRecordCreateRequest request) {
        Member member = findMember(userContext);
        ExerciseRecord exerciseRecord = request.toEntityWith(member);
        exerciseRecordRepository.save(exerciseRecord);
        return exerciseRecord.getId();
    }

    public RetrievePeriodExerciseRecordsResponse retrievePeriodExerciseRecords(UserContext userContext,
        DatePeriod datePeriod) {
        Member member = findMember(userContext);
        List<ExerciseRecord> exerciseRecords = exerciseRecordRepository.findAllByMemberAndPeriod(member,
            datePeriod.getStartDate(), datePeriod.getLastDate());
        Map<LocalDate, List<ExerciseRecord>> exerciseRecordMap = convertListToMapByDate(datePeriod, exerciseRecords);
        return RetrievePeriodExerciseRecordsResponse.from(exerciseRecordMap);
    }

    private Map<LocalDate, List<ExerciseRecord>> convertListToMapByDate(DatePeriod datePeriod,
        List<ExerciseRecord> exerciseRecords) {
        Map<LocalDate, List<ExerciseRecord>> result = createInitStatusMapByDate(datePeriod);
        for (ExerciseRecord exerciseRecord : exerciseRecords) {
            if (!result.containsKey(exerciseRecord.getRecordDate())) {
                throw new IllegalArgumentException("year와 month가 일치하지 않는 운동 결과가 발생했습니다.");
            }
            result.get(exerciseRecord.getRecordDate()).add(exerciseRecord);
        }
        return result;
    }

    private Map<LocalDate, List<ExerciseRecord>> createInitStatusMapByDate(DatePeriod datePeriod) {
        Map<LocalDate, List<ExerciseRecord>> result = new HashMap<>();
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
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(ErrorCode.RECORD_NOT_FOUND));
        if (!exerciseRecord.isOwnedBy(member)) {
            throw new ApiException(ErrorCode.AUTHORIZED_FAIL);
        }
        exerciseRecord.update(request.toEntityWith(member));
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

    private Member findMember(UserContext userContext) {
        return memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(ErrorCode.AUTHENTICATED_FAIL));
    }
}
