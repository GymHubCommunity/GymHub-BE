package com.example.temp.oauth.impl.kakao;

import com.example.temp.oauth.OAuthUserInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class KakaoUserInfo implements OAuthUserInfo {
    @JsonProperty("id")
    private String idUsingResourceServer;

    @JsonProperty("properties")
    private Properties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    static class Properties {
        @JsonProperty("nickname")
        private String name;

    }

    static class KakaoAccount {
        @JsonProperty("profile")
        private Profile profile;

        @Getter
        @JsonProperty("email")
        private String email;

        @Getter
        static class Profile {
            @JsonProperty("profile_image_url")
            private String profileUrl;
        }
    }

    @Override
    public String getProfileUrl() {
        return kakaoAccount.profile.getProfileUrl();
    }

    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Override
    public String getIdUsingResourceServer() {
        return idUsingResourceServer;
    }

    @Override
    public String getName() {
        return properties.getName();
    }
}

