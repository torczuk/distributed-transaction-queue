package com.github.torczuk

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.KafkaProducerConsumerIntegrationTest.LocalContext
import com.github.torczuk.configuration.AppConfiguration
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.EventListener
import com.github.torczuk.domain.EventProducer
import com.github.torczuk.infractructure.kafka.ConsumerConfiguration
import com.github.torczuk.infractructure.kafka.KafkaEventConsumer
import org.awaitility.Awaitility.await
import org.awaitility.Duration.ONE_SECOND
import org.awaitility.Duration.TEN_SECONDS
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.time.Instant
import java.util.*

@SpringBootTest(classes = [AppConfiguration::class, LocalContext::class])
internal class KafkaProducerConsumerIntegrationTest(
        @Autowired val publisher: EventProducer,
        @Autowired val eventListener: TestEventListener
) {
    val log = LoggerFactory.getLogger(KafkaProducerConsumerIntegrationTest::class.java)

    @Test
    fun `published booking event on topic should be eventually consumed by consumer listening on this topic`() {
        val now = Instant.now().toEpochMilli()
        val bookingEvent = BookingEvent(transaction = UUID.randomUUID().toString(), timestamp = now)

        publisher.publish(bookingEvent)

        await("published event is consumed").pollDelay(ONE_SECOND).atMost(TEN_SECONDS).until {
            log.info("consumed events: {}", eventListener.consumedEvents)
            eventListener.consumedEvents.contains(bookingEvent)
        }
    }


    class LocalContext {
        @Bean
        fun eventListener(): EventListener = TestEventListener()

        @Bean
        fun kafkaEventConsumer(listener: EventListener, objectMapper: ObjectMapper, threadPoolTaskExecutor: ThreadPoolTaskExecutor): KafkaEventConsumer {
            val consumer = KafkaEventConsumer(listener, ConsumerConfiguration(), objectMapper, "booking_events")
            threadPoolTaskExecutor.execute(consumer)
            return consumer
        }
    }
}