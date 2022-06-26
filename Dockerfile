FROM openjdk:11.0.15-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY run.sh run.sh
RUN chmod +x run.sh
CMD sh run.sh
