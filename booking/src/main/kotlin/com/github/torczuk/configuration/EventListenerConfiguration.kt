package com.github.torczuk.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.domain.BookingEventListener
import com.github.torczuk.domain.EventListener
import com.github.torczuk.infractructure.kafka.ConsumerConfiguration
import com.github.torczuk.infractructure.kafka.KafkaEventConsumer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class EventListenerConfiguration {

    @Bean
    fun kafkaEventConsumer(listener: EventListener, objectMapper: ObjectMapper, threadPoolTaskExecutor: ThreadPoolTaskExecutor): KafkaEventConsumer {
        val consumer = KafkaEventConsumer(listener, ConsumerConfiguration(), objectMapper, "booking_events")
        threadPoolTaskExecutor.execute(consumer)
        return consumer
    }

    @Bean
    fun eventListener() = BookingEventListener()

}

