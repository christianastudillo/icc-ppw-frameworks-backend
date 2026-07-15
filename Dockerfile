# ===== Etapa 1: Build =====
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /app

# Copiamos primero los archivos de configuración de Gradle para
# aprovechar el cache de capas de Docker (dependencias no cambian tan seguido)
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || return 0

# Ahora copiamos el resto del código fuente
COPY src ./src

# Compilamos el proyecto (sin correr los tests para agilizar el build)
RUN ./gradlew build -x test --no-daemon

# ===== Etapa 2: Runtime =====
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Copiamos únicamente el jar generado en la etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
