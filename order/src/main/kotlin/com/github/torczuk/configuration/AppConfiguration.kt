package com.github.torczuk.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.torczuk.domain.*
import com.github.torczuk.infractructure.kafka.ConsumerConfiguration
import com.github.torczuk.infractructure.kafka.KafkaEventProducer
import com.github.torczuk.infractructure.kafka.ProducerConfiguration
import com.github.torczuk.infrastructure.KafkaEventConsumer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.time.Clock
import java.time.ZoneId

@Configuration
class AppConfiguration {
    val log = LoggerFactory.getLogger(AppConfiguration::class.java)

    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())
        return mapper
    }

    @Bean
    fun orderEventProducer(objectMapper: ObjectMapper) = KafkaEventProducer<OrderEvent>(ProducerConfiguration(), "order_events", objectMapper)

    @Bean
    fun kafkaThreadExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 3
        executor.maxPoolSize = 3
        executor.setThreadNamePrefix("kafka_listener")
        executor.initialize()
        return executor
    }

    @Bean
    fun kafkaBookingEventConsumer(bookingEventListener: EventListener<BookingEvent>,
                                  objectMapper: ObjectMapper,
                                  threadPoolTaskExecutor: ThreadPoolTaskExecutor): KafkaEventConsumer<BookingEvent> {
        val consumer = KafkaEventConsumer(bookingEventListener,
                ConsumerConfiguration().properties(),
                objectMapper,
                "booking_events",
                BookingEvent::class.java)
        threadPoolTaskExecutor.execute(consumer)
        return consumer
    }

    @Bean
    fun bookingEventListener(orderEventProducer: KafkaEventProducer<OrderEvent>, clock: Clock): EventListener<BookingEvent> = BookingEventListener(orderEventProducer, clock)

    @Bean
    fun kafkaOrderEventConsumer(orderEventListener: EventListener<OrderEvent>, objectMapper: ObjectMapper, threadPoolTaskExecutor: ThreadPoolTaskExecutor): KafkaEventConsumer<OrderEvent> {
        val consumer = KafkaEventConsumer(orderEventListener,
                ConsumerConfiguration().properties(),
                objectMapper,
                "order_events",
                OrderEvent::class.java)
        threadPoolTaskExecutor.execute(consumer)
        return consumer
    }

    @Bean
    fun orderEventListener(orderEventRepository: OrderEventRepository, clock: Clock, orderEventProducer: KafkaEventProducer<OrderEvent>): EventListener<OrderEvent> {
        return OrderEventListener(orderEventRepository, orderEventProducer, clock)
    }

    @Bean
    fun paymentEventListener(orderEventProducer: KafkaEventProducer<OrderEvent>, clock: Clock): EventListener<PaymentEvent> = PaymentEventListener(orderEventProducer, clock)

    @Bean
    fun kafkaPaymentEventConsumer(paymentEventListener: EventListener<PaymentEvent>, objectMapper: ObjectMapper, threadPoolTaskExecutor: ThreadPoolTaskExecutor): KafkaEventConsumer<PaymentEvent> {
        val consumer = KafkaEventConsumer(paymentEventListener,
                ConsumerConfiguration().properties(),
                objectMapper,
                "payment_events",
                PaymentEvent::class.java)
        threadPoolTaskExecutor.execute(consumer)
        return consumer
    }

    @Bean
    fun clock() = Clock.system(ZoneId.of("UTC"))
}
