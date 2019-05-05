package com.github.torczuk.domain

import java.time.Clock

class BookingEventListener(val orderEventPublisher: EventProducer<OrderEvent>,
                           val clock: Clock) : EventListener<BookingEvent> {

    override fun accept(event: BookingEvent) {
        if (event.type == "created") {
            orderEventPublisher.publish(OrderEvent(event.transaction, "created", clock.millis()))
        }
    }
}