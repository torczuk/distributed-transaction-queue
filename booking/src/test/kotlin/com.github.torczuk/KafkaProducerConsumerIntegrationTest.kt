package com.github.torczuk

import com.github.torczuk.KafkaProducerConsumerIntegrationTest.LocalContext
import com.github.torczuk.configuration.AppConfiguration
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import com.github.torczuk.domain.EventProducer
import com.github.torczuk.util.InMemoryBookingEventRepository
import org.awaitility.Awaitility.await
import org.awaitility.Duration.*
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import java.time.Instant
import java.util.*

@SpringBootTest(classes = [AppConfiguration::class, LocalContext::class])
internal class KafkaProducerConsumerIntegrationTest(
        @Autowired val publisher: EventProducer,
        @Autowired val bookingEventRepository: BookingEventRepository
) {
    private val log = LoggerFactory.getLogger(KafkaProducerConsumerIntegrationTest::class.java)

    @Test
    fun `published booking event on topic should be eventually consumed by consumer listening on this topic`() {
        val now = Instant.now().toEpochMilli()
        val bookingEvent = BookingEvent(transaction = UUID.randomUUID().toString(), timestamp = now)

        publisher.publish(bookingEvent)

        await("published event is consumed").pollDelay(ONE_SECOND).atMost(ONE_MINUTE).until {
            log.info("consumed events: {}", bookingEventRepository.findAll())
            bookingEventRepository.exist(bookingEvent.transaction)
        }
    }

    @TestConfiguration
    class LocalContext {
        @Bean
        @Primary
        fun bookingEventRepository(): BookingEventRepository {
            return InMemoryBookingEventRepository()
        }
    }
}