package com.github.torczuk.domain

import com.github.torczuk.InMemoryProducer
import com.github.torczuk.util.Stubs.Companion.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class PaymentEventListenerTest {

    val producer = InMemoryProducer()
    val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
    val paymentEventListener = PaymentEventListener(producer, clock)

    @Test
    internal fun `should convert cancelled payment event to cancelled order event`() {
        val transaction = uuid()
        val paymentEvent = PaymentEvent(transaction, "cancelled", clock.millis())

        paymentEventListener.accept(paymentEvent)

        assertThat(producer.publishedEvents())
                .containsExactly(OrderEvent(transaction, "cancelled", clock.millis()))
    }
}