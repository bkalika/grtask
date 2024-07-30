FROM openjdk:21-jdk

WORKDIR /app

#ENV GIT_HUB_TOKEN=ghp_

COPY build/libs/gr-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
