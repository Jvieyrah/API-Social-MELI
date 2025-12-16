#build
FROM maven:3.9.7-eclipse-temurin AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src

RUN mvn package

#runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG  JAR_FILE=target/*.jar
COPY --from=build /app/${JAR_FILE} app.jar
#COPY --from=build /app/target/social-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]

LABEL authors="joaofilho"

