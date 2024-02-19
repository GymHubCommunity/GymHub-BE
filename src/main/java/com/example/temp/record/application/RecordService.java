package com.example.temp.record.application;

import com.example.temp.common.dto.UserContext;
import com.example.temp.record.domain.RecordRepository;
import com.example.temp.record.dto.request.RecordCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;

    @Transactional
    public long create(UserContext userContext, RecordCreateRequest request) {
        return 0;
    }
}
