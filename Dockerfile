FROM openjdk:17-alpine

WORKDIR /usr/src/app

ARG JAR_PATH=./build/libs

COPY ${JAR_PATH}/istock-0.0.1-SNAPSHOT.jar ${JAR_PATH}/istock-0.0.1-SNAPSHOT.jar

CMD ["java", "-Dspring.profiles.active=${SERVER_MODE}","-jar","./build/libs/istock-0.0.1-SNAPSHOT.jar"]
