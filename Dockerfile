# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiar solo el pom.xml primero para cachear dependencias
COPY pom.xml .
# Descargar dependencias (esto se cacheará si el pom no cambia)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar y empaquetar (saltando tests para agilizar build en CI/CD)
RUN mvn clean package -DskipTests

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear un usuario no root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el jar desde el stage de build
COPY --from=build /app/target/*.jar app.jar

# Variables de entorno por defecto (pueden ser sobrescritas por Render)
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Comando de ejecución optimizado para contenedores con poca memoria (512MB)
# -XX:MaxRAMPercentage=75.0: Usa el 75% de la RAM disponible para el Heap
# -XX:+UseContainerSupport: Habilita soporte de contenedor
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Xss512k", "-jar", "app.jar"]