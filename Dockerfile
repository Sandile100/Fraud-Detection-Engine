FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /build

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /build/fraud-boot/target/fraud-boot-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]