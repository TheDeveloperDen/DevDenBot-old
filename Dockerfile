FROM gradle:7.0.0-jdk11

WORKDIR /home/gradle/src
COPY . .

RUN gradle shadowJar --no-daemon

RUN mkdir /ap\data

COPY /home/gradle/src/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
