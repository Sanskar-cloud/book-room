FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ ./src/

RUN mvn clean package -DskipTests=true

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=build /app/target/bookroom-0.0.1-SNAPSHOT.jar bookroom.jar

ENTRYPOINT ["java", "-jar" ,"bookroom.jar"]

EXPOSE 8080