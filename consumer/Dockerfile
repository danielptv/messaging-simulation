FROM eclipse-temurin:19.0.2_7-jre-jammy
LABEL authors="Daniel Purtov"
WORKDIR /app
COPY /build/libs/consumer-1.0.0.jar /app/consumer.jar
CMD ["java", "--enable-preview", "-jar", "consumer.jar"]
