package com.github.torczuk.util

import com.github.torczuk.domain.OrderEvent
import com.github.torczuk.domain.OrderEventRepository

class InMemoryOrderEventRepository : OrderEventRepository {

    private val events = mutableListOf<OrderEvent>()

    override fun findBy(transactionId: String) = events.filter { event -> event.transaction == transactionId }

    override fun exist(transactionId: String) = events.any { event -> event.transaction == transactionId }

    override fun findAll() = events

    override fun save(event: OrderEvent) {
        events.add(event)
    }
}