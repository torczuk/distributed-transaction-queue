package com.github.torczuk

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.Publisher
import java.time.Clock

class BookingService(val clock: Clock, val publisher: Publisher) {

    fun create(txdId: String): BookingEvent {
        val bookingEvent = BookingEvent(transaction = txdId, timestamp = clock.instant().toEpochMilli())
        publisher.publish(bookingEvent)
        return bookingEvent
    }
}