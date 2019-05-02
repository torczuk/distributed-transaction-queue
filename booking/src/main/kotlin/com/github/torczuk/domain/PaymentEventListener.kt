package com.github.torczuk.domain

import org.slf4j.LoggerFactory
import java.time.Clock

class PaymentEventListener(
        private val bookingEventProducer: EventProducer<BookingEvent>,
        private val clock: Clock) : EventListener<PaymentEvent> {

    private val log = LoggerFactory.getLogger(PaymentEventListener::class.java)

    override fun accept(event: PaymentEvent) {
        log.info("processing: {}", event)
        if (event.type == "cancelled") {
            log.info("cancelling booking, because of: {}", event)
            bookingEventProducer.publish(BookingEvent(event.transaction, "cancelled", clock.millis()))
        }
        if (event.type == "confirmed") {
            log.info("confirmed booking, because of: {}", event)
            bookingEventProducer.publish(BookingEvent(event.transaction, "confirmed", clock.millis()))
        }
    }
}