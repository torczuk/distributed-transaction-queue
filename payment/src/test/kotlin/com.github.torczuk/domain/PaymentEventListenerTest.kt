package com.github.torczuk.domain

import com.github.torczuk.InMemoryProducer
import com.github.torczuk.util.InMemoryPaymentEventRepository
import com.github.torczuk.util.Stubs
import com.github.torczuk.util.Stubs.Companion.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class PaymentEventListenerTest {

    val paymentEventRepository = InMemoryPaymentEventRepository()
    val producer = InMemoryProducer()
    val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
    val paymentEventListener = PaymentEventListener(paymentEventRepository, producer, clock)

    @Test
    fun `created event should be persisted and converted to confirmed`() {
        val createdEventTimestamp = 1L
        val transactionId = uuid()
        val createdEvent = PaymentEvent(transaction = transactionId, timestamp = createdEventTimestamp)

        paymentEventListener.accept(createdEvent)

        val confirmedEvent = PaymentEvent(transactionId, "confirmed", clock.millis())
        assertThat(producer.publishedEvents()).containsExactly(confirmedEvent)
        assertThat(paymentEventRepository.findAll()).contains(createdEvent)
    }

    @Test
    fun `confirmed should be only persisted`() {
        val confirmedEvent = PaymentEvent(uuid(), "confirmed", clock.millis())

        paymentEventListener.accept(confirmedEvent)

        assertThat(producer.publishedEvents()).isEmpty()
        assertThat(paymentEventRepository.findAll()).contains(confirmedEvent)
    }

    @Test
    @Disabled("enable when functionality is ready")
    fun `created event with invalid id should be converted to cancelled event`() {
        val invalidId = Stubs.id().toString()
        val invalidEvent = PaymentEvent(invalidId)

        paymentEventListener.accept(invalidEvent)

        assertThat(producer.publishedEvents()).containsExactly(invalidEvent.copy(type = "cancelled"))
        assertThat(paymentEventRepository.findAll()).isEmpty()
    }
}