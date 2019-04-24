package com.github.torczuk

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import com.github.torczuk.domain.EventProducer
import com.github.torczuk.util.Stubs
import com.github.torczuk.util.Stubs.Companion.uuid
import org.awaitility.Awaitility.await
import org.awaitility.Duration.ONE_MINUTE
import org.awaitility.Duration.ONE_SECOND
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import java.time.Instant
import java.util.*

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [TestContext::class])
internal class KafkaProducerConsumerIntegrationTest(
        @Autowired val publisher: EventProducer,
        @Autowired val bookingEventRepository: BookingEventRepository
) {
    private val log = LoggerFactory.getLogger(KafkaProducerConsumerIntegrationTest::class.java)

    @Test
    fun `published booking event on topic should be eventually consumed by consumer listening on this topic`() {
        val now = Instant.now().toEpochMilli()
        val bookingEvent = BookingEvent(transaction = uuid(), timestamp = now)

        publisher.publish(bookingEvent)

        await("published event is consumed").pollDelay(ONE_SECOND).atMost(ONE_MINUTE).until {
            log.info("consumed events: {}", bookingEventRepository.findAll())
            bookingEventRepository.exist(bookingEvent.transaction)
        }
    }
}