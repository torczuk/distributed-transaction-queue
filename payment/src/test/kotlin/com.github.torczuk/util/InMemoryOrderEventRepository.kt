package com.github.torczuk.util

import com.github.torczuk.domain.PaymentEvent
import com.github.torczuk.domain.PaymentEventRepository

class InMemoryPaymentEventRepository : PaymentEventRepository {

    private val events = mutableListOf<PaymentEvent>()

    override fun findBy(transactionId: String) = events.filter { event -> event.transaction == transactionId }

    override fun exist(transactionId: String) = events.any { event -> event.transaction == transactionId }

    override fun findAll() = events

    override fun save(event: PaymentEvent) {
        events.add(event)
    }
}