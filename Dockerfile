FROM neduekwunife/openjdk8-jre-alpine-with-fontconfig

RUN mkdir /app
VOLUME /var/data

COPY ./ /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
