FROM amazoncorretto:21-alpine-jdk
MAINTAINER joony <bestheroz@gmail.com>
COPY build/libs/*.jar app.jar

EXPOSE 8000

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar -server -Dfile.encoding=UTF-8" ]
