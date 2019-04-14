FROM openjdk:11-slim

WORKDIR /app
ADD build/libs/app*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]