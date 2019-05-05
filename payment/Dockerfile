FROM openjdk:11-slim

WORKDIR /app
ADD build/libs/payment*.jar payment.jar

EXPOSE 8080

CMD ["java", "-jar", "payment.jar"]