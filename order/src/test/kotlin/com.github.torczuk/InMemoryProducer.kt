package com.github.torczuk

import com.github.torczuk.domain.EventProducer
import com.github.torczuk.domain.OrderEvent
import java.util.*

class InMemoryProducer : EventProducer<OrderEvent> {
    val deque: Deque<OrderEvent> = ArrayDeque<OrderEvent>()

    override fun publish(event: OrderEvent) {
        deque.offerFirst(event)
    }

    fun publishedEvents() = deque

}