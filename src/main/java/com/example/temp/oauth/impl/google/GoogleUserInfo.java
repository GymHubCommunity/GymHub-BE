package com.example.temp.oauth.impl.google;

import com.example.temp.oauth.OAuthUserInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleUserInfo implements OAuthUserInfo {
    @JsonProperty("picture")
    String profileUrl;

    @JsonProperty("email")
    String email;

    @JsonProperty("sub")
    String idUsingResourceServer;

    @JsonProperty("name")
    String name;

    @Override
    public String getProfileUrl() {
        return profileUrl;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getIdUsingResourceServer() {
        return idUsingResourceServer;
    }

    @Override
    public String getName() {
        return name;
    }
}
