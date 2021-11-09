FROM gradle:7.2.0-jdk11 AS build

WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .

RUN gradle installDist --no-daemon

FROM openjdk:17-jdk

RUN mkdir /app
VOLUME /var/data

COPY --from=build /home/gradle/src/build/install/DevDenBot /app

ENTRYPOINT ["/app/bin/DevDenBot"]
