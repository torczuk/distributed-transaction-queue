package com.github.torczuk.infractructure.http

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.torczuk.TestContext
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.util.Stubs.Companion.uuid
import org.awaitility.Awaitility.await
import org.awaitility.Duration
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [TestContext::class])
class TransactionControllerIntegrationTest(
        @Autowired private val restTemplate: TestRestTemplate,
        @LocalServerPort private val randomServerPort: Int,
        @Autowired private val objectMapper: ObjectMapper) {

    private val log = LoggerFactory.getLogger(TransactionControllerIntegrationTest::class.java)

    @Test
    fun `transaction POSTed on the transaction endpoint should eventually be available by GET`() {
        val transaction = uuid()
        val response = POST("/api/v1/transaction/$transaction")

        await("posted transaction is available").pollDelay(Duration.ONE_SECOND).atMost(Duration.ONE_MINUTE).until {
            val statuses = GET(location(response.body))
            log.info("status for {}: {}", transaction, statuses.body)
            isTransactionInBody(statuses.body, transaction)
        }
    }

    private fun POST(uri: String) = restTemplate.postForEntity<String>(
            "http://localhost:$randomServerPort/$uri",
            null,
            String::class.java)

    private fun GET(uri: String) = restTemplate.getForEntity(
            "http://localhost:$randomServerPort/$uri",
            String::class.java)

    private fun location(body: String?) = objectMapper.readValue<Map<String, String>>(body!!)["location"]!!

    private fun isTransactionInBody(body: String?, transactionId: String): Boolean {
        val transactions: List<BookingEvent> = objectMapper.readValue(body!!)
        return transactions.any { it.transaction == transactionId }
    }
}

inline fun <reified T> ObjectMapper.readValue(s: String): T =
        this.readValue(s, object : TypeReference<T>() {})
