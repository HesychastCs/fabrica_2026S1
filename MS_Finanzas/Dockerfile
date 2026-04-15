FROM ubuntu:latest
LABEL authors="jesustakethewheel09"

ENTRYPOINT ["top", "-b"]

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn -B -DskipTests clean package && ls -la target/
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080
EXPOSE 8080

ENV JAVA_OPTS="-Xms300m -Xmx300m"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]