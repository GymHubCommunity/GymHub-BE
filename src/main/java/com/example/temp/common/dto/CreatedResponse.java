package com.example.temp.common.dto;

public record CreatedResponse(
    long id
) {

    public static CreatedResponse of(long id) {
        return new CreatedResponse(id);
    }
}
