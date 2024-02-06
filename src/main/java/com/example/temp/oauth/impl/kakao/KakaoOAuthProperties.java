package com.example.temp.oauth.impl.kakao;

import java.util.Arrays;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoOAuthProperties(
    String fromUri,
    String clientId,
    String clientSecret,
    String redirectUri,
    String[] scope
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KakaoOAuthProperties that = (KakaoOAuthProperties) o;
        return Objects.equals(fromUri, that.fromUri) && Objects.equals(clientId, that.clientId)
            && Objects.equals(clientSecret, that.clientSecret) && Objects.equals(redirectUri,
            that.redirectUri) && Arrays.equals(scope, that.scope);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fromUri, clientId, clientSecret, redirectUri);
        result = 31 * result + Arrays.hashCode(scope);
        return result;
    }

    @Override
    public String toString() {
        return "KakaoOAuthProperties{" +
            "fromUri='" + fromUri + '\'' +
            ", clientId='" + clientId + '\'' +
            ", clientSecret='" + clientSecret + '\'' +
            ", redirectUri='" + redirectUri + '\'' +
            ", scope=" + Arrays.toString(scope) +
            '}';
    }
}
