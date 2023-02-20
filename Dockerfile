FROM --platform=linux/arm64 openjdk:17

WORKDIR /usr/src/app

ARG JAR_PATH=./build/libs

COPY ${JAR_PATH}/iStock-Backend-0.0.1-SNAPSHOT.jar ${JAR_PATH}/iStock-Backend-0.0.1-SNAPSHOT.jar

CMD ["java", "-Dspring.profiles.active=${SERVER_MODE}", "-Dspring.datasource.username=${MYSQL_USERNAME}", "-Dspring.datasource.password=${MYSQL_PASSWORD}", "-jar","./build/libs/iStock-Backend-0.0.1-SNAPSHOT.jar"]