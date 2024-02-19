package com.example.temp.record.application;

import com.example.temp.record.dto.request.RecordCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordService {

    public long create(RecordCreateRequest request) {
        return 0;
    }
}
