package com.github.torczuk.infractructure.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.util.HttpTest
import com.github.torczuk.util.Stubs.Companion.uuid
import org.awaitility.Awaitility.await
import org.awaitility.Duration
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TransactionControllerIntegrationTest(
        @Autowired override val restTemplate: TestRestTemplate,
        @LocalServerPort override val serverPort: Int,
        @Autowired val objectMapper: ObjectMapper) : HttpTest {

    private val log = LoggerFactory.getLogger(TransactionControllerIntegrationTest::class.java)

    @Test
    fun `transaction POSTed on the transaction endpoint should eventually be available by GET`() {
        val transaction = uuid()
        log.info("POST $transaction ...")
        val response = POST("/api/v1/transaction/$transaction")

        await("posted transaction is available").pollDelay(Duration.ONE_SECOND).atMost(Duration.ONE_MINUTE).until {
            val statuses = GET(location(response.body))
            log.info("status for {}: {}", transaction, statuses.body)
            containsTransaction(statuses.body, transaction)
        }
    }

    private fun location(body: String?) = objectMapper.readValue<Map<String, String>>(body!!)["location"]!!

    private fun containsTransaction(body: String?, transactionId: String): Boolean {
        val transactions: List<BookingEvent> = objectMapper.readValue(body!!)
        return transactions.any { it.transaction == transactionId }
    }
}
