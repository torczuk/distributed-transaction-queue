package com.github.torczuk.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.torczuk.domain.*
import com.github.torczuk.infractructure.kafka.ConsumerConfiguration
import com.github.torczuk.infractructure.kafka.KafkaEventConsumer
import com.github.torczuk.infractructure.kafka.KafkaEventProducer
import com.github.torczuk.infractructure.kafka.ProducerConfiguration
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
    fun paymentEventProducer(objectMapper: ObjectMapper) = KafkaEventProducer<PaymentEvent>(ProducerConfiguration(), "payment_events", objectMapper)

    @Bean
    fun kafkaThreadExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 2
        executor.setThreadNamePrefix("kafka_listener")
        executor.initialize()
        return executor
    }

    @Bean
    fun kafkaOrderEventConsumer(orderEventListener: EventListener<OrderEvent>,
                                objectMapper: ObjectMapper,
                                threadPoolTaskExecutor: ThreadPoolTaskExecutor): KafkaEventConsumer<OrderEvent> {
        val consumer = KafkaEventConsumer(orderEventListener,
                ConsumerConfiguration(),
                objectMapper,
                "order_events",
                OrderEvent::class.java)
        threadPoolTaskExecutor.execute(consumer)
        return consumer
    }

    @Bean
    fun orderEventListener(paymentEventProducer: KafkaEventProducer<PaymentEvent>, clock: Clock): EventListener<OrderEvent> = OrderEventListener(paymentEventProducer, clock)

    @Bean
    fun kafkaPaymentEventConsumer(paymentEventListener: EventListener<PaymentEvent>, objectMapper: ObjectMapper, threadPoolTaskExecutor: ThreadPoolTaskExecutor): KafkaEventConsumer<PaymentEvent> {
        val consumer = KafkaEventConsumer(paymentEventListener,
                ConsumerConfiguration(),
                objectMapper,
                "payment_events",
                PaymentEvent::class.java)
        threadPoolTaskExecutor.execute(consumer)
        return consumer
    }

    @Bean
    fun paymentEventListener(paymentEventRepository: PaymentEventRepository, clock: Clock, paymentEventProducer: KafkaEventProducer<PaymentEvent>): EventListener<PaymentEvent> {
        return PaymentEventListener(paymentEventRepository, paymentEventProducer, clock)
    }

    @Bean
    fun clock() = Clock.system(ZoneId.of("UTC"))
}
