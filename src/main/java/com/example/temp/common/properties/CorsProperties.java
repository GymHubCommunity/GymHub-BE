package com.example.temp.common.properties;

import java.util.Arrays;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
    String[] allowedOrigins,
    String[] allowedMethods,
    String[] allowedHeaders
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CorsProperties that = (CorsProperties) o;
        return Arrays.equals(allowedOrigins, that.allowedOrigins) && Arrays.equals(allowedMethods,
            that.allowedMethods) && Arrays.equals(allowedHeaders, that.allowedHeaders);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(allowedOrigins);
        result = 31 * result + Arrays.hashCode(allowedMethods);
        result = 31 * result + Arrays.hashCode(allowedHeaders);
        return result;
    }

    @Override
    public String toString() {
        return "CorsProperties{" +
            "allowedOrigins=" + Arrays.toString(allowedOrigins) +
            ", allowedMethods=" + Arrays.toString(allowedMethods) +
            ", allowedHeaders=" + Arrays.toString(allowedHeaders) +
            '}';
    }
}
