package com.github.torczuk.infractructure.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.torczuk.TestAppContext
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.EventProducer
import com.github.torczuk.util.HttpTest
import com.github.torczuk.util.Stubs.Companion.uuid
import org.awaitility.Awaitility.await
import org.awaitility.Duration.ONE_MINUTE
import org.awaitility.Duration.ONE_SECOND
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import java.time.Clock

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [TestAppContext::class])
class OrderControllerIntegrationTest(
        @Autowired override val restTemplate: TestRestTemplate,
        @LocalServerPort override val serverPort: Int,
        @Autowired val objectMapper: ObjectMapper,
        @Autowired val bookingEventProducer: EventProducer<BookingEvent>,
        @Autowired val clock: Clock) : HttpTest {

    private val log = LoggerFactory.getLogger(OrderControllerIntegrationTest::class.java)

    @Test
    fun `booking event published should be processed and eventually available by GET`() {
        val transaction = uuid()
        log.info("Publishing $transaction ...")
        bookingEventProducer.publish(BookingEvent(transaction = transaction, timestamp = clock.millis()))

        await("posted transaction is available").pollDelay(ONE_SECOND).atMost(ONE_MINUTE).until {
            val statuses = GET("api/v1/orders/$transaction")
            log.info("status for {}: {}", transaction, statuses.body)
            containsTransaction(statuses.body, transaction)
        }
    }

    private fun containsTransaction(body: String?, transactionId: String): Boolean {
        val transactions: List<BookingEvent> = objectMapper.readValue(body!!)
        return transactions.any { it.transaction == transactionId }
    }
}
