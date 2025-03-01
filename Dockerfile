FROM maven:3.8.5-openjdk-17 AS dependencies

WORKDIR /opt/app
COPY pom.xml .

RUN mvn dependency:go-offline

FROM maven:3.8.5-openjdk-17 AS builder

WORKDIR /opt/app
COPY --from=dependencies /root/.m2 /root/.m2
COPY --from=dependencies /opt/app/ /opt/app
COPY ./src ./src

RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /opt/app/target/*.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]