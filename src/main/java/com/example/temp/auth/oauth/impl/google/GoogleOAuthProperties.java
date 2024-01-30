package com.example.temp.auth.oauth.impl.google;

import java.util.Arrays;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
public record GoogleOAuthProperties(
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
        GoogleOAuthProperties that = (GoogleOAuthProperties) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(clientSecret,
            that.clientSecret) && Objects.equals(redirectUri, that.redirectUri) && Arrays.equals(scope,
            that.scope);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(clientId, clientSecret, redirectUri);
        result = 31 * result + Arrays.hashCode(scope);
        return result;
    }

    @Override
    public String toString() {
        return "GoogleOAuthProperties{" +
            "clientId='" + clientId + '\'' +
            ", clientSecret='" + clientSecret + '\'' +
            ", redirectUri='" + redirectUri + '\'' +
            ", scope=" + Arrays.toString(scope) +
            '}';
    }
}