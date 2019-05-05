package com.github.torczuk

import com.github.torczuk.domain.EventProducer
import com.github.torczuk.domain.OrderEvent
import com.github.torczuk.domain.PaymentEvent
import java.time.Clock

class PaymentService(val clock: Clock, val publisher: EventProducer<PaymentEvent>) {

    fun create(txdId: String): PaymentEvent {
        val paymentEvent = PaymentEvent(transaction = txdId, timestamp = clock.instant().toEpochMilli())
        publisher.publish(paymentEvent)
        return paymentEvent
    }
}