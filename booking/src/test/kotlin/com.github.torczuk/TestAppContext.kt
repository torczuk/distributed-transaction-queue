package com.github.torczuk

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.domain.OrderEvent
import com.github.torczuk.domain.PaymentEvent
import com.github.torczuk.infractructure.kafka.KafkaEventProducer
import com.github.torczuk.infractructure.kafka.ProducerConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestAppContext {
    @Bean
    fun orderEventProducer(objectMapper: ObjectMapper) = KafkaEventProducer<OrderEvent>(ProducerConfiguration(), "order_events", objectMapper)

    @Bean
    fun paymentEventProducer(objectMapper: ObjectMapper) = KafkaEventProducer<PaymentEvent>(ProducerConfiguration(), "payment_events", objectMapper)

}