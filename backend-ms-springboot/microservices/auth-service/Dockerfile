FROM eclipse-temurin:25-jdk-jammy AS build

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends maven ca-certificates && rm -rf /var/lib/apt/lists/*

COPY . .

RUN mvn clean package -DskipTests -pl microservices/auth-service -am

FROM eclipse-temurin:25-jre-jammy

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/microservices/auth-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]