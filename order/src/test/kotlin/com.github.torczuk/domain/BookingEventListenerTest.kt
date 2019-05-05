package com.github.torczuk.domain

import com.github.torczuk.InMemoryProducer
import com.github.torczuk.util.Stubs.Companion.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class BookingEventListenerTest {

    private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
    private val orderEventPublisher = InMemoryProducer()
    private val bookingEventListener = BookingEventListener(orderEventPublisher, clock)


    @Test
    internal fun `should transform created booking event to created order event`() {
        val transaction = uuid()
        val createdBookingEvent = BookingEvent(transaction, "created", clock.millis())

        bookingEventListener.accept(createdBookingEvent)

        assertThat(orderEventPublisher.publishedEvents())
                .containsExactly(OrderEvent(transaction, "created", clock.millis()))
    }

    @ParameterizedTest
    @ValueSource(strings = ["confirmed", "cancelled", "any other"])
    internal fun `should ignore any other types like confirmed or cancelled`(type: String) {
        val bookingEvent = BookingEvent(uuid(), type, clock.millis())

        bookingEventListener.accept(bookingEvent)

        assertThat(orderEventPublisher.publishedEvents()).isEmpty()
    }
}