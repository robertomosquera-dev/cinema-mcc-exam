FROM eclipse-temurin:25-jdk-noble AS build

WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/
RUN ./mvnw -B -q -DskipTests package

FROM eclipse-temurin:25-jre-noble

WORKDIR /app
COPY --from=build /workspace/target/booking-service-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 51200

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
