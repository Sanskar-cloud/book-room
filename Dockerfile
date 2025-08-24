FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ ./src/

RUN mvn clean package -DskipTests=true

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=build /app/target/finalProject-0.0.1-SNAPSHOT.jar finalProject.jar

ENTRYPOINT ["java", "-jar" ,"finalProject.jar"]

EXPOSE 8080