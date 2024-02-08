package com.example.temp.auth.infrastructure;

import com.example.temp.auth.dto.MemberInfo;

public interface TokenParser {

    long parse(String token);

    MemberInfo parsedClaims(String token);
}
