package com.github.torczuk.domain

import com.github.torczuk.domain.util.Stubs.Companion.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class OrderEventTest {

    val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))

    @Test
    internal fun `should confirmed created event`() {
        val transaction = uuid()
        val created = OrderEvent(transaction, "created")

        val confirmed = created.confirm(clock.millis())

        assertThat(confirmed).isEqualTo(OrderEvent(transaction, "confirmed", clock.millis()))
    }

    @Test
    internal fun `should cancel created event`() {
        val transaction = uuid()
        val created = OrderEvent(transaction, "created")

        val cancelled = created.cancel(clock.millis())

        assertThat(cancelled).isEqualTo(OrderEvent(transaction, "cancelled", clock.millis()))
    }
}