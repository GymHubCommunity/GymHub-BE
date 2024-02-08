package com.example.temp.common.convertor;

import com.example.temp.member.domain.PrivacyPolicy;
import org.springframework.core.convert.converter.Converter;

public class StringToPrivacyPolicyConverter implements Converter<String, PrivacyPolicy> {

    @Override
    public PrivacyPolicy convert(String source) {
        return PrivacyPolicy.valueOf(source.trim().toUpperCase());
    }
}
