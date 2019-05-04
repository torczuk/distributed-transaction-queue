package com.github.torczuk

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.infractructure.kafka.KafkaEventProducer
import com.github.torczuk.infractructure.kafka.ProducerConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestAppContext {
    @Bean
    fun bookingEventProducer(objectMapper: ObjectMapper) = KafkaEventProducer<BookingEvent>(ProducerConfiguration(), "booking_events", objectMapper)
}