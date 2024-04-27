FROM eclipse-temurin:21.0.2_13-jdk-jammy as jre-build
WORKDIR /app

COPY gradle/ gradle/
COPY gradlew gradle.properties settings.gradle ./

# Download the gradle wrapper
RUN ./gradlew wrapper || true

COPY build.gradle ./
RUN ./gradlew downloadBuildDependencies --no-daemon

COPY ./src ./src

RUN ./gradlew bootJar --no-daemon --offline


FROM eclipse-temurin:21-jre-alpine as jre-alpine 
WORKDIR /app

COPY --from=jre-build /app/build/libs/bot-service.jar .

EXPOSE 8080

CMD ["java", "-jar", "bot-service.jar"]


FROM gcr.io/distroless/java21-debian12:nonroot as jre-distroless 
WORKDIR /app    

COPY --from=jre-build /app/build/libs/bot-service.jar .

EXPOSE 8080

CMD ["bot-service.jar"]

