# Usamos Eclipse Temurin (OpenJDK estándar) versión 21 sobre Ubuntu Jammy (LTS)
# Usamos JRE en lugar de JDK para que la imagen sea más liviana
FROM eclipse-temurin:21-jre-jammy

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado.
# ⚠️ IMPORTANTE: Verifica que el nombre "sut_backend-0.0.1-SNAPSHOT.jar"
# coincida con lo que tienes en tu carpeta 'build/libs/'
COPY build/libs/SUT_backend-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto interno de Spring Boot
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
