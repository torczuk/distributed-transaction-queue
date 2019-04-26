package com.github.torczuk.infractructure.http

import com.github.torczuk.NoStartEventConsumer
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import com.github.torczuk.domain.EventProducer
import com.github.torczuk.util.Stubs.Companion.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.OK
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Clock
import java.time.Instant

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [NoStartEventConsumer::class])
class TransactionControllerTest(
        @Autowired private val restTemplate: TestRestTemplate,
        @LocalServerPort private val randomServerPort: Int,
        @Autowired val clock: Clock) {

    @MockBean
    private lateinit var repository: BookingEventRepository
    @MockBean
    private lateinit var eventProducer: EventProducer

    @Test
    fun shouldAcceptTransactionToBeProcessedAndResponseWithStatusLocation() {
        val transaction = uuid()

        val response = POST("/api/v1/transaction/$transaction")

        verify(eventProducer).publish(BookingEvent(transaction, "created", clock.millis()))
        assertThat(response.statusCode).isEqualTo(ACCEPTED)
        assertThat(response.body).isEqualTo("""{"location": "/api/v1/transaction/$transaction"}""".trimIndent())
    }

    @Test
    fun shouldResponseTransactionStatus() {
        val transaction = uuid()
        val now = Instant.now().toEpochMilli()
        given(repository.findBy(transaction)).willReturn(listOf(BookingEvent(transaction, "created", now)))

        val response = GET("/api/v1/transaction/$transaction")

        assertThat(response.statusCode).isEqualTo(OK)
        assertThat(response.body).isEqualTo("""[{"transaction":"$transaction","type":"created","timestamp":$now}]""".trimIndent())
    }

    private fun POST(uri: String) = restTemplate.postForEntity<String>(
            "http://localhost:$randomServerPort/$uri",
            null,
            String::class.java)

    private fun GET(uri: String) = restTemplate.getForEntity(
            "http://localhost:$randomServerPort/$uri",
            String::class.java)
}