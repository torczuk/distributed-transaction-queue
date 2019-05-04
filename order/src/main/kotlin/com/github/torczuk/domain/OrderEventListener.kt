package com.github.torczuk.domain

import org.slf4j.LoggerFactory
import java.time.Clock

class OrderEventListener(
        val orderEventRepository: OrderEventRepository,
        val orderEventProducer: EventProducer<OrderEvent>,
        val clock: Clock) : EventListener<OrderEvent> {
    val log = LoggerFactory.getLogger(OrderEventListener::class.java)

    override fun accept(event: OrderEvent) {
        log.info("processing {} ...", event)
        orderEventRepository.save(event)
        if (event.type == "created") {
            orderEventProducer.publish(OrderEvent(event.transaction, "confirmed", clock.millis()))
        }
    }
}