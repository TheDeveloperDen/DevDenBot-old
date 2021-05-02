FROM gradle:7.0.0-jdk11

WORKDIR /home/gradle/src
COPY . .

RUN gradle shadowJar --no-daemon

RUN mkdir /app/data

COPY ./*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
