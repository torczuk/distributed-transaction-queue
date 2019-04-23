package com.github.torczuk

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.OK
import java.util.*

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [TestContext::class])
internal class TransactionControllerTest(
        @Autowired private val restTemplate: TestRestTemplate,
        @LocalServerPort private val randomServerPort: Int) {

    @Test
    fun shouldAcceptTransactionToBeProcessedAndResponseWithStatusLocation() {
        val transaction = UUID.randomUUID().toString()

        val response = POST("/api/v1/transaction/$transaction")

        assertThat(response.statusCode).isEqualTo(ACCEPTED)
        assertThat(response.body).isEqualTo("""{"location": "/api/v1/transaction/$transaction"}""".trimIndent())
    }

    @Test
    fun shouldResponseTransactionStatus() {
        val transaction = UUID.randomUUID().toString()

        val response = GET("/api/v1/transaction/$transaction")

        assertThat(response.statusCode).isEqualTo(OK)
        assertThat(response.body).isEqualTo("""{"status": "in progress"}""".trimIndent())
    }

    private fun POST(uri: String) = restTemplate.postForEntity<String>(
            "http://localhost:$randomServerPort/$uri",
            null,
            String::class.java)


    private fun GET(uri: String) = restTemplate.getForEntity(
            "http://localhost:$randomServerPort/$uri",
            String::class.java)
}