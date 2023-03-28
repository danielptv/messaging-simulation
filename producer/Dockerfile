FROM eclipse-temurin:19.0.2_7-jre-jammy
LABEL authors="Daniel Purtov"
WORKDIR /app
COPY /build/libs/producer-1.0.0.jar /app/producer.jar
EXPOSE 8080
CMD ["java", "--enable-preview", "-jar", "producer.jar"]
