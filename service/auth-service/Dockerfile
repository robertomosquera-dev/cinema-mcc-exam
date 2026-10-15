FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src/ src/
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:25-jre-noble
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /workspace/target/auth-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 50003
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
