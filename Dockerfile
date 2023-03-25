FROM openjdk:17
WORKDIR /app
COPY build/libs/habr-0.0.1-SNAPSHOT.jar /app/application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]