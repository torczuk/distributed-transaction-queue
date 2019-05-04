package com.github.torczuk.domain

import com.github.torczuk.InMemoryProducer
import com.github.torczuk.util.InMemoryOrderEventRepository
import com.github.torczuk.util.Stubs.Companion.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class OrderEventListenerTest {

    val orderEventRepository = InMemoryOrderEventRepository()
    val producer = InMemoryProducer()
    val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
    val orderEventListener = OrderEventListener(orderEventRepository, producer, clock)

    @Test
    fun `created event should be persisted and converted to confirmed`() {
        val createdEventTimestamp = 1L
        val transactionId = uuid()
        val createdEvent = OrderEvent(transaction = transactionId, timestamp = createdEventTimestamp)

        orderEventListener.accept(createdEvent)

        val confirmedEvent = OrderEvent(transactionId, "confirmed", clock.millis())
        assertThat(producer.publishedEvents()).containsExactly(confirmedEvent)
        assertThat(orderEventRepository.findAll()).contains(createdEvent)
    }

    @Test
    fun `confirmed should be only persisted`() {
        val confirmedEvent = OrderEvent(uuid(), "confirmed", clock.millis())

        orderEventListener.accept(confirmedEvent)

        assertThat(producer.publishedEvents()).isEmpty()
        assertThat(orderEventRepository.findAll()).contains(confirmedEvent)
    }
}