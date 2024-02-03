FROM gradle:8.5-jdk17 AS build

WORKDIR /app

COPY . /app

RUN gradle clean build --no-daemon

FROM amazoncorretto:17.0.10-alpine3.19

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/gymhub.jar

EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "gymhub.jar"]