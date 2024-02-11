package com.example.temp.common.config;

import com.example.temp.common.properties.S3Properties;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider, S3Configuration s3Configuration) {
        return S3Presigner.builder()
            .region(Region.of(s3Properties.region()))
            .endpointOverride(URI.create(s3Properties.endpoint()))
            .credentialsProvider(credentialsProvider)
            .serviceConfiguration(s3Configuration)
            .build();
    }

    /**
     * 로컬 환경에서는 S3를 에뮬레이팅합니다. 해당 환경에서는 Credentials가 필요 없어 test, test라는 값을 넣어 사용합니다.
     */
    @Bean
    @Profile("local")
    @SuppressWarnings("java:S6437")
    public AwsCredentialsProvider localAwsCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"));
    }

    /**
     * 로컬 환경에서 S3를 대신해서 LocalStack을 사용중입니다. LocalStack은 s3와 경로가 다르다는 문제가 있어 별도의 프로필로 관리합니다.
     */
    @Bean
    @Profile("local")
    public S3Configuration localS3Configuration() {
        return S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build();
    }

    @Bean
    @Profile("!local")
    public AwsCredentialsProvider prodAwsCredentialsProvider() {
        return InstanceProfileCredentialsProvider.create();
    }

    @Bean
    @Profile("!local")
    public S3Configuration prodS3Configuration() {
        return S3Configuration.builder()
            .build();
    }
}
