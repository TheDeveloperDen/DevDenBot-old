FROM gradle:7.0.0-jdk11 AS build

WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .

RUN gradle clean shadowJar

FROM neduekwunife/openjdk8-jre-alpine-with-fontconfig

RUN mkdir /app
VOLUME /var/data

COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
