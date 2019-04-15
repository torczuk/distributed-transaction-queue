package com.github.torczuk

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.EventProducer
import java.util.*

class InMemoryProducer: EventProducer {
    val deque: Deque<BookingEvent> = ArrayDeque<BookingEvent>()

    override fun publish(bookingEvent: BookingEvent) {
        deque.offerFirst(bookingEvent)
    }

    fun publishedEvents() = deque

}