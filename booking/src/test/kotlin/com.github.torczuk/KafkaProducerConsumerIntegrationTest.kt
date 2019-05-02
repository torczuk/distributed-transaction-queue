package com.github.torczuk

import com.github.torczuk.domain.*
import com.github.torczuk.util.Stubs.Companion.uuid
import org.awaitility.Awaitility.await
import org.awaitility.Duration.ONE_SECOND
import org.awaitility.Duration.TEN_SECONDS
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import java.time.Instant.now

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [TestAppContext::class])
internal class KafkaProducerConsumerIntegrationTest(
        @Autowired val bookingEventProducer: EventProducer<BookingEvent>,
        @Autowired val orderEventProducer: EventProducer<OrderEvent>,
        @Autowired val paymentEventProducer: EventProducer<PaymentEvent>,
        @Autowired val bookingEventRepository: BookingEventRepository
) {
    private val log = LoggerFactory.getLogger(KafkaProducerConsumerIntegrationTest::class.java)

    @Test
    fun `created booking event should be eventually consumed and persisted as created`() {
        val createdBooking = BookingEvent(transaction = uuid(), timestamp = now().toEpochMilli())
        log.info("publishing $createdBooking")

        bookingEventProducer.publish(createdBooking)

        await("created booking is persisted").pollDelay(ONE_SECOND).atMost(TEN_SECONDS).until {
            log.info("consumed events: {}", bookingEventRepository.findAll())
            bookingEventRepository.exist(createdBooking.transaction)
        }
    }

    @Test
    fun `created booking event should be confirmed by confirmed payment`() {
        val transactionId = uuid()
        val createdBookingEvent = BookingEvent(transactionId, timestamp = now().toEpochMilli())
        val createdPaymentEvent = PaymentEvent(transactionId, "confirmed", timestamp = now().toEpochMilli())

        bookingEventProducer.publish(createdBookingEvent)
        paymentEventProducer.publish(createdPaymentEvent)

        await("confirmed booking is persisted").pollDelay(ONE_SECOND).atMost(TEN_SECONDS).until {
            log.info("consumed events: {}", bookingEventRepository.findAll())
            bookingEventRepository.findBy(transactionId).any { event -> event.type == "confirmed" }
                    && bookingEventRepository.findBy(transactionId).none { event -> event.type == "cancelled" }
        }
    }

    @Test
    fun `created booking event should be cancelled by cancelled order event`() {
        val transactionId = uuid()
        val createdBookingEvent = BookingEvent(transactionId, timestamp = now().toEpochMilli())
        val cancelledOrderEvent = OrderEvent(transactionId, "cancelled", timestamp = now().toEpochMilli())

        bookingEventProducer.publish(createdBookingEvent)
        orderEventProducer.publish(cancelledOrderEvent)

        await("cancelled booking is persisted").pollDelay(ONE_SECOND).atMost(TEN_SECONDS).until {
            log.info("consumed events: {}", bookingEventRepository.findAll())
            bookingEventRepository.findBy(transactionId).none { event -> event.type == "confirmed" }
                    && bookingEventRepository.findBy(transactionId).any { event -> event.type == "cancelled" }
        }
    }

    @Test
    fun `created booking event should be cancelled by cancelled payment event`() {
        val transactionId = uuid()
        val createdBookingEvent = BookingEvent(transactionId, timestamp = now().toEpochMilli())
        val createdOrderEvent = OrderEvent(transactionId, timestamp = now().toEpochMilli())
        val cancelledPaymentEvent = PaymentEvent(transactionId, "cancelled", timestamp = now().toEpochMilli())

        bookingEventProducer.publish(createdBookingEvent)
        orderEventProducer.publish(createdOrderEvent)
        paymentEventProducer.publish(cancelledPaymentEvent)

        await("cancelled booking is persisted").pollDelay(ONE_SECOND).atMost(TEN_SECONDS).until {
            log.info("consumed events: {}", bookingEventRepository.findAll())
            bookingEventRepository.findBy(transactionId).none { event -> event.type == "confirmed" }
                    && bookingEventRepository.findBy(transactionId).any { event -> event.type == "cancelled" }
        }
    }
}