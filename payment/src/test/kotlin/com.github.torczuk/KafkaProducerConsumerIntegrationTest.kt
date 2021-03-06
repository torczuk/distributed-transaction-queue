package com.github.torczuk

import com.github.torczuk.domain.EventProducer
import com.github.torczuk.domain.PaymentEvent
import com.github.torczuk.domain.PaymentEventRepository
import com.github.torczuk.util.Stubs.Companion.uuid
import org.awaitility.Awaitility.await
import org.awaitility.Duration.ONE_MINUTE
import org.awaitility.Duration.ONE_SECOND
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.TestContext
import java.time.Instant

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [TestAppContext::class])
internal class KafkaProducerConsumerIntegrationTest(
        @Autowired val paymentEventProducer: EventProducer<PaymentEvent>,
        @Autowired val paymentEventRepository: PaymentEventRepository
) {
    private val log = LoggerFactory.getLogger(KafkaProducerConsumerIntegrationTest::class.java)

    @Test
    fun `published order event on topic should be eventually consumed by consumer listening on this topic`() {
        val now = Instant.now().toEpochMilli()
        val event = PaymentEvent(transaction = uuid(), timestamp = now)
        log.info("publishing $event")

        paymentEventProducer.publish(event)

        await("published event is consumed and persisted").pollDelay(ONE_SECOND).atMost(ONE_MINUTE).until {
            log.info("consumed events: {}", paymentEventRepository.findAll())
            paymentEventRepository.exist(event.transaction)
        }
    }
}