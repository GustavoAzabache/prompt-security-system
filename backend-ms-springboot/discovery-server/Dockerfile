FROM eclipse-temurin:25-jdk-jammy AS build

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends maven ca-certificates && rm -rf /var/lib/apt/lists/*

COPY . .

RUN mvn clean package -DskipTests -pl discovery-server -am

FROM eclipse-temurin:25-jre-jammy

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/discovery-server/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]