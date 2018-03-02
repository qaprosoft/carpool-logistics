FROM openjdk:8-jdk-alpine

ENV CPL_GM_KEY=not_set
ENV CPL_SERVER_PORT=8080
ENV CPL_SERVER_PATH=
ENV CPL_DB_URL=not_set
ENV CPL_DB_USER=not_set
ENV CPL_DB_PASS=not_set

VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
EXPOSE 8080

COPY entrypoint.sh /
RUN chmod +x /entrypoint.sh
RUN dos2unix /entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]