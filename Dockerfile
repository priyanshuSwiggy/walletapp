# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim
LABEL authors="priyanshu"

# Set the working directory in the container
WORKDIR /app

# Copy the Maven build file and install dependencies
COPY pom.xml ./
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
RUN mvn dependency:go-offline

# Copy the project source code into the container
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# Copy the JAR to the container
COPY target/walletapp-0.0.1-SNAPSHOT.jar /app/walletapp.jar

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "/app/walletapp.jar"]
