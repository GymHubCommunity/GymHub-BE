# 빌드 스테이지
FROM amazoncorretto:17.0.10-alpine AS builder
WORKDIR /app

# Gradle Wrapper 복사
COPY gradlew .
COPY gradle gradle
RUN chmod +x ./gradlew

# 의존성 파일 복사 및 다운로드
COPY build.gradle .
COPY settings.gradle .

RUN ./gradlew --no-daemon dependencies

# 소스코드 복사 및 애플리케이션 빌드
COPY . .
RUN ./gradlew --nodaemon clean build

# 실행 스테이지
FROM amazoncorretto:17.0.10-alpine
COPY --from=builder /app/build/libs/*.jar /app/gymhub.jar
ENTRYPOINT ["java"]
CMD ["-jar", "/app/gymhub.jar"]