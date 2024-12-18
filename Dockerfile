FROM openjdk:17-jdk-slim
LABEL authors="acer"
WORKDIR /app
COPY build/libs/homework_tasks.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
