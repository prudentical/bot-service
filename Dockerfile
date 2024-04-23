FROM gradle:8.7.0-jdk21-graal-jammy as jre-build
WORKDIR /app

# Download and cache dependencies in a separate layer
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

COPY ./src ./src

RUN gradle bootJar --no-daemon


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

