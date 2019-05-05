package com.github.torczuk

import com.github.torczuk.domain.OrderEvent
import com.github.torczuk.domain.PaymentEvent
import com.github.torczuk.util.Stubs.Companion.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class PaymentServiceTest {
    val producer = InMemoryProducer()
    val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))

    val paymentService = PaymentService(clock, producer)

    @Test
    internal fun `published order should be available by consumer`() {
        val transactionId = uuid()
        val expectedTimestamp = Instant.now(clock).toEpochMilli()

        val event = paymentService.create(transactionId)

        assertThat(event).isEqualTo(PaymentEvent(transactionId, "created", expectedTimestamp))
        assertThat(producer.publishedEvents().first).isEqualTo(event)
    }
}