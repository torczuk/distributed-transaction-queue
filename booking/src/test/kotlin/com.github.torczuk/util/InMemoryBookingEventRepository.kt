package com.github.torczuk.util

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository

class InMemoryBookingEventRepository : BookingEventRepository {

    private val events = mutableListOf<BookingEvent>()

    override fun findBy(transactionId: String) = events.findLast { event -> event.transaction == transactionId }

    override fun exist(transactionId: String) = events.any { event -> event.transaction == transactionId }

    override fun findAll() = events

    override fun save(event: BookingEvent) {
        events.add(event)
    }

}