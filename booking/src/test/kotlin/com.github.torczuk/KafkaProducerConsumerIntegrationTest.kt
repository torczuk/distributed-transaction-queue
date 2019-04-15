package com.github.torczuk

import com.github.torczuk.KafkaProducerConsumerIntegrationTest.LocalContext
import com.github.torczuk.configuration.AppConfiguration
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.EventListener
import com.github.torczuk.domain.EventProducer
import org.awaitility.Awaitility.await
import org.awaitility.Duration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import java.time.Instant
import java.util.*

@SpringBootTest(classes = [AppConfiguration::class, LocalContext::class])
internal class KafkaProducerConsumerIntegrationTest(
        @Autowired val publisher: EventProducer,
        @Autowired val eventListener: TestEventListener
) {
    @Test
    fun `published booking event on topic should be eventually consumed by consumer listening on this topic`() {
        val now = Instant.now().toEpochMilli()
        val bookingEvent = BookingEvent(transaction = UUID.randomUUID().toString(), timestamp = now)

        publisher.publish(bookingEvent)

        await("published event is consumed").atLeast(Duration.TEN_SECONDS).until {
            eventListener.consumedEvents.contains(bookingEvent)
        }
    }


    class LocalContext {
        @Bean
        fun eventListener(): EventListener = TestEventListener()
    }
}