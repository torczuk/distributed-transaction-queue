FROM openjdk:11-slim

WORKDIR /app
ADD build/libs/order*.jar order.jar

EXPOSE 8080

CMD ["java", "-jar", "order.jar"]