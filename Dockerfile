FROM gradle:7.0.0-jdk11 as cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle.kts /home/gradle/bot-code/
COPY settings.gradle.kts /home/gradle/bot-code/
COPY gradle.properties /home/gradle/bot-code/
WORKDIR /home/gradle/bot-code
RUN gradle clean build -i --stacktrace


FROM gradle:7.0.0-jdk11 AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle shadowJar


FROM neduekwunife/openjdk8-jre-alpine-with-fontconfig
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
