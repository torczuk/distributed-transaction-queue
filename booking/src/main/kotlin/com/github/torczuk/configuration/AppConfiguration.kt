package com.github.torczuk.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.torczuk.domain.BookingEventListener
import com.github.torczuk.domain.BookingEventRepository
import com.github.torczuk.domain.EventListener
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
    fun eventProducer(objectMapper: ObjectMapper) = KafkaEventProducer(ProducerConfiguration(), "booking_events", objectMapper)

    @Bean
    fun kafkaThreadExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 1
        executor.maxPoolSize = 1
        executor.setThreadNamePrefix("kafka_listener")
        executor.initialize()
        return executor
    }

    @Bean
    fun kafkaEventConsumer(listener: EventListener, objectMapper: ObjectMapper, threadPoolTaskExecutor: ThreadPoolTaskExecutor): KafkaEventConsumer {
        val consumer = KafkaEventConsumer(listener, ConsumerConfiguration(), objectMapper, "booking_events")
        threadPoolTaskExecutor.execute(consumer)
        return consumer
    }

    @Bean
    fun listener(bookingEventRepository: BookingEventRepository): EventListener {
        log.info("Injecting booking repository  {}", bookingEventRepository.javaClass)
        return BookingEventListener(bookingEventRepository)
    }

    @Bean
    fun clock() = Clock.system(ZoneId.of("UTC"))
}
