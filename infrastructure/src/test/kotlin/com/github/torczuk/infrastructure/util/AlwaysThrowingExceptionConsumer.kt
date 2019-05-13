package com.github.torczuk.infrastructure.util

import com.github.torczuk.domain.BookingEvent
import java.util.function.Consumer

class AlwaysThrowingExceptionConsumer : Consumer<BookingEvent> {
    val events = mutableListOf<BookingEvent>()
    override fun accept(event: BookingEvent) {
        events.add(event)
        throw RuntimeException("something went wrong :(")
    }
}