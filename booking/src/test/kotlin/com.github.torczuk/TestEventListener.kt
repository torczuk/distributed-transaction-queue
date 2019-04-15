package com.github.torczuk

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.EventListener

class TestEventListener : EventListener {
    val consumedEvents = mutableListOf<BookingEvent>()

    override fun accept(event: BookingEvent) {
        consumedEvents.add(event)
    }
}