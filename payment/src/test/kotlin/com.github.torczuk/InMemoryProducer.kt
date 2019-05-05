package com.github.torczuk

import com.github.torczuk.domain.EventProducer
import com.github.torczuk.domain.PaymentEvent
import java.util.*

class InMemoryProducer : EventProducer<PaymentEvent> {
    val deque: Deque<PaymentEvent> = ArrayDeque<PaymentEvent>()

    override fun publish(event: PaymentEvent) {
        deque.offerFirst(event)
    }

    fun publishedEvents() = deque

}