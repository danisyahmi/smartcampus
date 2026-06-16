# Stage 1: Build the JAX-WS Application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Compile and package the application
RUN mvn clean package -DskipTests

# Stage 2: Run the Standalone SOAP Server
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the compiled jar from the build stage
COPY --from=build /app/target/library_system-1.0-SNAPSHOT.jar app.jar

# Expose the JAX-WS port we assigned (8888)
EXPOSE 8888

# Execute the main class
ENTRYPOINT ["java", "-jar", "app.jar"]