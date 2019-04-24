package com.github.torczuk

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.util.Stubs.Companion.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class BookingServiceTest {
    val producer = InMemoryProducer()
    val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))

    val bookingService = BookingService(clock, producer)

    @Test
    internal fun `published booking should be available by consumer`() {
        val transactionId = uuid()
        val expectedTimestamp = Instant.now(clock).toEpochMilli()

        val event = bookingService.create(transactionId)

        assertThat(event).isEqualTo(BookingEvent(transactionId, "created", expectedTimestamp))
        assertThat(producer.publishedEvents().first).isEqualTo(event)
    }
}