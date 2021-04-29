FROM gradle:latest
FROM neduekwunife/openjdk8-jre-alpine-with-fontconfig
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src


RUN mkdir /app
VOLUME /var/data

ENTRYPOINT ["./gradlew", "run"]
