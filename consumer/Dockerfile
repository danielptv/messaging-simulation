# Build stage
FROM gradle:jdk19 AS BUILD_IMAGE
WORKDIR /app
COPY . /app
RUN gradle clean build

# Package stage
FROM eclipse-temurin:19.0.2_7-jre-jammy
WORKDIR /app
COPY --from=BUILD_IMAGE /app/build/libs/consumer-1.0.0.jar /app/consumer.jar
CMD ["java", "--enable-preview", "-jar", "consumer.jar"]
