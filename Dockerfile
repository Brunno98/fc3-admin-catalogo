FROM eclipse-temurin:17-alpine-3.21

COPY build/libs/*.jar /opt/app/application.jar

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

CMD ["java", "-jar", "/opt/app/application.jar"]