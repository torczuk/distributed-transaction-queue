package com.github.torczuk.domain

import org.slf4j.LoggerFactory
import java.time.Clock

class OrderEventListener(
        private val bookingEventProducer: EventProducer<BookingEvent>,
        private val clock: Clock) : EventListener<OrderEvent> {

    private val log = LoggerFactory.getLogger(OrderEventListener::class.java)

    override fun accept(event: OrderEvent) {
        log.info("processing: {}", event)
        if (event.type == "cancelled") {
            log.info("cancelling booking, because of: {}", event)
            bookingEventProducer.publish(BookingEvent(event.transaction, "cancelled", clock.millis()))
        }
    }
}