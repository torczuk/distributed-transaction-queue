package com.github.torczuk

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.Publisher
import java.util.*

class InMemoryProducer(private val deque: Deque<BookingEvent>) : Publisher {

    override fun publish(bookingEvent: BookingEvent) {
        deque.offerFirst(bookingEvent)
    }
}