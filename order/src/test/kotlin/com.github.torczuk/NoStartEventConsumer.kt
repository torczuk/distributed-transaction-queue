package com.github.torczuk

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.domain.EventListener
import com.github.torczuk.domain.OrderEvent
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
    fun kafkaEventConsumer(listener: EventListener<OrderEvent>, objectMapper: ObjectMapper, threadPoolTaskExecutor: ThreadPoolTaskExecutor) = KafkaEventConsumer(listener, ConsumerConfiguration(), objectMapper, "order_events")

    @Bean
    fun clock() = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
}