package com.github.torczuk.domain

import org.slf4j.LoggerFactory
import java.time.Clock
import java.util.*

class PaymentEventListener(
        private val paymentEventRepository: PaymentEventRepository,
        private val paymentEventProducer: EventProducer<PaymentEvent>,
        private val clock: Clock) : EventListener<PaymentEvent> {

    private val log = LoggerFactory.getLogger(PaymentEventListener::class.java)

    override fun accept(event: PaymentEvent) {
        log.info("processing {} ...", event)
        paymentEventRepository.save(event)
        if (event.type == "created") {
            if (valid(event)) {
                paymentEventProducer.publish(PaymentEvent(event.transaction, "confirmed", clock.millis()))
            } else {
                paymentEventProducer.publish(PaymentEvent(event.transaction, "cancelled", clock.millis()))
            }
        }
    }

    private fun valid(event: PaymentEvent): Boolean {
        try {
            UUID.fromString(event.transaction)
        } catch (ex: Exception) {
            return false
        }
        return true
    }
}