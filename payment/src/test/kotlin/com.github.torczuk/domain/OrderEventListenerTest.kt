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

internal class OrderEventListenerTest {

    private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
    private val paymentEventPublisher = InMemoryProducer()
    private val orderEventListener = OrderEventListener(paymentEventPublisher, clock)


    @Test
    internal fun `should transform confirmed order event to created payment event`() {
        val transaction = uuid()
        val createdOrderEvent = OrderEvent(transaction, "confirmed", clock.millis())

        orderEventListener.accept(createdOrderEvent)

        assertThat(paymentEventPublisher.publishedEvents())
                .containsExactly(PaymentEvent(transaction, "created", clock.millis()))
    }

    @ParameterizedTest
    @ValueSource(strings = ["created", "cancelled", "any other"])
    internal fun `should ignore any other types like created or cancelled`(type: String) {
        val orderEvent = OrderEvent(uuid(), type, clock.millis())

        orderEventListener.accept(orderEvent)

        assertThat(paymentEventPublisher.publishedEvents()).isEmpty()
    }
}