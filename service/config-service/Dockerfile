FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/
RUN ./mvnw -B -q -DskipTests package

FROM eclipse-temurin:25-jre-noble
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /workspace/target/config-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 50000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
