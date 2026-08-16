FROM eclipse-temurin:26

# these lines:
#   ARG JAR_FILE=target/*.jar
#   COPY ${JAR_FILE} app.jar
# can be simplified to
#   COPY target/*.jar app.jar

ARG JAR_FILE=target/*.jar
# only one .jar file is expected, otherwise the error will be thrown
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]