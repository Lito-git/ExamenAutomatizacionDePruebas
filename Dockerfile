# Imagen base con el runtime de Java 21
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia las clases compiladas por Maven en el pipeline
COPY target/classes /app/classes

# Versión de la aplicación, informada por el pipeline al construir la imagen
ARG APP_VERSION=local
ENV APP_VERSION=${APP_VERSION}

# SIMULACIÓN: configuración de despliegue errónea (puerto distinto al publicado)
ENV PORT=9090

# Puerto en el que escucha el servidor de login
EXPOSE 8080

# Inicia el servidor de login
CMD ["java", "-cp", "/app/classes", "com.iplacex.login.ServidorLogin"]