package com.example.temp.auth.infrastructure;

import com.example.temp.common.dto.UserContext;

public interface TokenParser {

    long parse(String token);

    UserContext parsedClaims(String token);
}
