package com.github.torczuk

import com.github.torczuk.domain.EventProducer
import com.github.torczuk.domain.OrderEvent
import com.github.torczuk.domain.OrderEventRepository
import com.github.torczuk.util.Stubs.Companion.uuid
import org.awaitility.Awaitility.await
import org.awaitility.Duration.ONE_MINUTE
import org.awaitility.Duration.ONE_SECOND
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import java.time.Instant

@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class KafkaProducerConsumerIntegrationTest(
        @Autowired val publisher: EventProducer<OrderEvent>,
        @Autowired val orderEventRepository: OrderEventRepository
) {
    private val log = LoggerFactory.getLogger(KafkaProducerConsumerIntegrationTest::class.java)

    @Test
    fun `published booking event on topic should be eventually consumed by consumer listening on this topic`() {
        val now = Instant.now().toEpochMilli()
        val event = OrderEvent(transaction = uuid(), timestamp = now)
        log.info("publishing $event")

        publisher.publish(event)

        await("published event is consumed and persisted").pollDelay(ONE_SECOND).atMost(ONE_MINUTE).until {
            log.info("consumed events: {}", orderEventRepository.findAll())
            orderEventRepository.exist(event.transaction)
        }
    }
}