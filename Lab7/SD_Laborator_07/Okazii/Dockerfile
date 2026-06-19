# Folosim o imagine oficială de Java 17 (sau ce versiune folosești tu)
FROM eclipse-temurin:17-jre-alpine

# Setăm folderul de lucru în interiorul containerului
WORKDIR /app

# Creăm folderul pentru loguri
RUN mkdir Logs

# Primim ca argument calea către fișierul JAR și îl copiem în container
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# Comanda de rulare a microserviciului
ENTRYPOINT ["java", "-jar", "app.jar"]