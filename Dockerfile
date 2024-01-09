# Utiliza la imagen base de OpenJDK 17
FROM openjdk:17

# Copia el JAR de la aplicación al contenedor
COPY ./docker-spring-boot.jar /app/docker-spring-boot.jar

# Añade wait-for-it
# ADD https://github.com/vishnubob/wait-for-it/raw/master/wait-for-it.sh /app/wait-for-it.sh
# RUN chmod +x /app/wait-for-it.sh

# Expone el puerto 8080
EXPOSE 8080

# Comando para esperar a que la base de datos esté disponible y luego ejecutar la aplicación
# CMD ["/app/wait-for-it.sh", "buitnmd3o4sbbbgkhipt-mysql.services.clever-cloud.com:3306/buitnmd3o4sbbbgkhipt", "--timeout=30", "--", "java", "-jar", "/app/docker-spring-boot.jar"]
