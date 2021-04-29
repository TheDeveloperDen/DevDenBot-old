FROM gradle:latest AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle shadowJar --no-daemon

FROM neduekwunife/openjdk8-jre-alpine-with-fontconfig

RUN mkdir /app
VOLUME /var/data

COPY --from=build /home/gradle/src/build/libs/*.jar /app/jeeves.jar

ENTRYPOINT ["java", "-jar", "/app/jeeves.jar"]
