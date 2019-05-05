package com.github.torczuk.domain

import org.slf4j.LoggerFactory
import java.time.Clock

class BookingEventListener(
        private val orderEventPublisher: EventProducer<OrderEvent>,
        private val clock: Clock) : EventListener<BookingEvent> {

    private val log = LoggerFactory.getLogger(BookingEventListener::class.java)

    override fun accept(event: BookingEvent) {
        log.info("processing {} ...", event)
        if (event.type == "created") {
            orderEventPublisher.publish(OrderEvent(event.transaction, "created", clock.millis()))
        }
    }
}