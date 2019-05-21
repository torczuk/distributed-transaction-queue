package com.github.torczuk.domain

import org.slf4j.LoggerFactory
import java.time.Clock

class OrderEventListener(
        private val orderEventRepository: OrderEventRepository,
        private val orderEventProducer: EventProducer<OrderEvent>,
        private val clock: Clock) : EventListener<OrderEvent> {

    private val log = LoggerFactory.getLogger(OrderEventListener::class.java)

    override fun accept(event: OrderEvent) {
        log.info("processing {} ...", event)
        orderEventRepository.save(event)
        if (event.type == "created") {
            orderEventProducer.publish(event.confirm(clock.millis()))
        }
    }
}