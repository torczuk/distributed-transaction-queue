package com.github.torczuk

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.EventListener
import com.github.torczuk.domain.OrderEvent
import com.github.torczuk.domain.PaymentEvent
import com.github.torczuk.infractructure.kafka.ConsumerConfiguration
import com.github.torczuk.infractructure.kafka.KafkaEventConsumer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@TestConfiguration
class NoStartEventConsumer {
    @Bean
    fun kafkaBookingEventConsumer(bookingEventListener: EventListener<BookingEvent>,
                                  objectMapper: ObjectMapper,
                                  threadPoolTaskExecutor: ThreadPoolTaskExecutor) = KafkaEventConsumer(bookingEventListener, ConsumerConfiguration(), objectMapper, "booking_events", BookingEvent::class.java)

    @Bean
    fun kafkaOrderEventConsumer(orderEventListener: EventListener<OrderEvent>,
                                objectMapper: ObjectMapper,
                                threadPoolTaskExecutor: ThreadPoolTaskExecutor) = KafkaEventConsumer(orderEventListener, ConsumerConfiguration(), objectMapper, "order_events", OrderEvent::class.java)

    @Bean
    fun kafkaPaymentEventConsumer(paymentEventListener: EventListener<PaymentEvent>,
                                  objectMapper: ObjectMapper,
                                  threadPoolTaskExecutor: ThreadPoolTaskExecutor) = KafkaEventConsumer(paymentEventListener, ConsumerConfiguration(), objectMapper, "payment_events", PaymentEvent::class.java)


    @Bean
    fun clock() = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
}