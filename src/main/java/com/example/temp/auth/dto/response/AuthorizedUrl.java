package com.example.temp.auth.dto.response;

public record AuthorizedUrl(
    String authorizedUrl
) {

    public static AuthorizedUrl createInstance(String authorizedUrl) {
        return new AuthorizedUrl(authorizedUrl);
    }
}
