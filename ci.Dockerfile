# Package stage
FROM amazoncorretto:21-al2023-headless

LABEL org.opencontainers.image.source="https://github.com/ohbus/link.whoa.sh"

WORKDIR /app

COPY build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-XX:MaxRAMPercentage=90.0"

EXPOSE 8844

ENTRYPOINT ["java", "-jar", "app.jar"]
