package com.github.torczuk

import com.github.torczuk.domain.EventProducer
import com.github.torczuk.domain.OrderEvent
import java.time.Clock

class OrderService(val clock: Clock, val publisher: EventProducer<OrderEvent>) {

    fun create(txdId: String): OrderEvent {
        val orderEvent = OrderEvent(transaction = txdId, timestamp = clock.instant().toEpochMilli())
        publisher.publish(orderEvent)
        return orderEvent
    }
}