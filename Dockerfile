FROM gradle:7.0.0-jdk11 AS build

WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .

RUN gradle shadowJar

FROM openjdk:17-jdk

RUN mkdir /app
VOLUME /var/data

COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--add-exports", "java.desktop/sun.font=ALL-UNNAMED"]
