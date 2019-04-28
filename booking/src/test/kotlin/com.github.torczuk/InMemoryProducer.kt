package com.github.torczuk

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.EventProducer
import java.util.*

class InMemoryProducer: EventProducer<BookingEvent> {
    val deque: Deque<BookingEvent> = ArrayDeque<BookingEvent>()

    override fun publish(event: BookingEvent) {
        deque.offerFirst(event)
    }

    fun publishedEvents() = deque

}