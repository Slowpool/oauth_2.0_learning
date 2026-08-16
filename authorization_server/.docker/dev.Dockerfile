FROM maven:3.9-eclipse-temurin-26 AS external_and_standard_libs

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:26

WORKDIR /app

COPY --from=external_and_standard_libs /app/target/*.jar app.jar

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-Dspring.profiles.active=dev", "-jar", "/app/app.jar"]


# ########### SECOND OPTION
# FROM maven:3.9.16

# WORKDIR /app

# COPY . .

# CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.profiles=dev", \
#      "-Dspring.devtools.restart.enabled=true", \
#      "-Dspring.devtools.livereload.enabled=true", \
#      "-Dspring.devtools.remote.secret=mysecret"]


########## FIRST OPTION
# FROM maven:3.9.6-eclipse-temurin-17 AS dev
# WORKDIR /app

# # Copy configuration metadata first to leverage cache layers
# COPY pom.xml .
# RUN mvn dependency:go-offline

# # Copy the actual source directory
# COPY src ./src

# # Expose app port and DevTools remote traffic port (if needed)
# EXPOSE 8080

# # Run Spring Boot via plugin to allow live reloading
# CMD ["mvn", "spring-boot:run"]