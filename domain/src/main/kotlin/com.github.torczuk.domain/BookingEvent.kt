package com.github.torczuk.domain

import java.time.Instant

data class BookingEvent(override val transaction: String,
                        override val type: String = "created",
                        override val timestamp: Long = Instant.now().toEpochMilli()) : Event {

    override fun confirm(timestamp: Long): BookingEvent = copy(type = "confirmed", timestamp = timestamp)
    override fun cancel(timestamp: Long): BookingEvent = copy(type = "cancelled", timestamp = timestamp)
}