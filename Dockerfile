FROM openjdk:17-alpine

WORKDIR /usr/src/app

ARG JAR_PATH=./build/libs

COPY ${JAR_PATH}/iStock-Backend-0.0.1-SNAPSHOT.jar ${JAR_PATH}/iStock-Backend-0.0.1-SNAPSHOT.jar

CMD ["java", "-Dspring.profiles.active=${SERVER_MODE}","-jar","./build/libs/iStock-Backend-0.0.1-SNAPSHOT.jar"]