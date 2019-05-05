package com.github.torczuk.domain

import org.slf4j.LoggerFactory
import java.time.Clock

class PaymentEventListener(
        private val paymentEventRepository: PaymentEventRepository,
        private val paymentEventProducer: EventProducer<PaymentEvent>,
        private val clock: Clock) : EventListener<PaymentEvent> {

    private val log = LoggerFactory.getLogger(PaymentEventListener::class.java)

    override fun accept(event: PaymentEvent) {
        log.info("processing {} ...", event)
        paymentEventRepository.save(event)
        if (event.type == "created") {
            paymentEventProducer.publish(PaymentEvent(event.transaction, "confirmed", clock.millis()))
        }
    }
}