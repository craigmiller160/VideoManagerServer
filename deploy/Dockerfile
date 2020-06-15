FROM openjdk:11.0.2-jre-slim

RUN mkdir -p /opt/videos
RUN mkdir -p /opt/video-manager/server-logs
RUN mkdir -p /opt/video-manager/homeDir
COPY build/video-manager-server-*.jar /video-manager-server.jar

ENTRYPOINT ["java", "-jar", "/video-manager-server.jar"]