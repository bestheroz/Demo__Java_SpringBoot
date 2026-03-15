FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app
COPY build/libs/*.jar app.jar

FROM eclipse-temurin:25-jre-alpine
LABEL maintainer="joony <bestheroz@gmail.com>"
COPY --from=builder /app/app.jar /app.jar

EXPOSE 8000

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar -server -Dfile.encoding=UTF-8" ]
