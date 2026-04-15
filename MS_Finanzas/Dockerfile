FROM ubuntu:latest
LABEL authors="jesustakethewheel09"

ENTRYPOINT ["top", "-b"]

FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto (Render usará uno dinámico, pero esto es buena práctica)
EXPOSE 8080

# Comando para arrancar la aplicación
# Limitamos la memoria RAM para que no se pase de los 512MB de Render
ENTRYPOINT ["java", "-Xmx300m", "-Xms300m", "-jar", "app.jar"]