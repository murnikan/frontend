# сборка jar
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# запуск
FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /build/target/frontend-*.jar /app/app.jar
ENV BACKEND_URL=http://backend:8080
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
