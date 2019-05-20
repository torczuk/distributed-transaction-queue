package com.github.torczuk.domain

import org.slf4j.LoggerFactory
import java.time.Clock

class PaymentEventListener(
        private val orderEventProducer: EventProducer<OrderEvent>,
        private val clock: Clock) : EventListener<PaymentEvent> {

    private val log = LoggerFactory.getLogger(PaymentEventListener::class.java)

    override fun accept(event: PaymentEvent) {
        log.info("processing {} ...", event)
        if (event.type == "cancelled") {
            orderEventProducer.publish(OrderEvent(event.transaction, "cancelled", clock.millis()))
        }
    }
}