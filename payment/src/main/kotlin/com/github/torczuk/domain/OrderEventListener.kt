package com.github.torczuk.domain

import org.slf4j.LoggerFactory
import java.time.Clock

class OrderEventListener(
        private val paymentEventPublisher: EventProducer<PaymentEvent>,
        private val clock: Clock) : EventListener<OrderEvent> {

    private val log = LoggerFactory.getLogger(OrderEventListener::class.java)

    override fun accept(event: OrderEvent) {
        log.info("processing {} ...", event)
        if (event.type == "confirmed") {
            paymentEventPublisher.publish(PaymentEvent(event.transaction, "created", clock.millis()))
        }
    }
}