FROM openjdk:11-slim

WORKDIR /app
ADD build/libs/booking*.jar booking.jar

EXPOSE 8080

CMD ["java", "-jar", "booking.jar"]