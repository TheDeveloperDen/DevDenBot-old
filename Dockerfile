FROM gradle:7.0.0-jdk11 AS build

WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .

RUN gradle installDist

FROM openjdk:17-jdk

RUN mkdir /app
VOLUME /var/data

COPY --from=build /home/gradle/src/build/install/DevDenBot /app

ENTRYPOINT ["JAVA_OPTS='--add-exports java.desktop/sun.font=ALL-UNNAMED' /app/bin/DevDenBot"]
