# --- Stage 1: Build ---
# Usamos una imagen de Gradle con JDK 17
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app

# Copiar los archivos de configuración de Gradle y el wrapper
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle/

# Copiar el código fuente
COPY src ./src

# Ejecutar el build de Gradle.
# --no-daemon es recomendado para entornos CI/CD y contenedores.
# -x test salta la ejecución de tests para un build más rápido.
RUN ./gradlew build --no-daemon -x test

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear un usuario no root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el jar desde el stage de build (la ruta cambia a build/libs)
COPY --from=build /app/build/libs/*.jar app.jar

# Variables de entorno por defecto (pueden ser sobrescritas por Render)
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Comando de ejecución optimizado para contenedores con poca memoria (512MB)
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Xss512k", "-jar", "app.jar"]