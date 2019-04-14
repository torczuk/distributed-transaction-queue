package com.github.torczuk

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.Consumer
import java.util.*

class InMemoryConsumer(private val deque: Deque<BookingEvent>) : Consumer {
    override fun get(): BookingEvent = deque.removeLast()
}