FROM openjdk:17-jdk-alpine
# Install curl for healthcheck
RUN apk add --no-cache curl
VOLUME /tmp
COPY *.jar Solicitudes.jar
ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
# Crear un usuario
RUN addgroup -g 1001 appgroup && adduser -D -u 1001 -G appgroup appuser
# Crear el directorio de logs y dar propiedad a appuser
RUN mkdir -p /build/logs && chown -R appuser:appgroup /build
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar Solicitudes.jar" ]
