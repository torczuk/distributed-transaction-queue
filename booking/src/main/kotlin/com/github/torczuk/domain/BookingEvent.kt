package com.github.torczuk.domain

import java.time.Instant

data class BookingEvent(val transaction: String,
                        val type: String = "created",
                        val timestamp: Long = Instant.now().toEpochMilli()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookingEvent

        if (transaction != other.transaction) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = transaction.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}