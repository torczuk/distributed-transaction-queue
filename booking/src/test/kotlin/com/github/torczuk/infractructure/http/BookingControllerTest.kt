package com.github.torczuk.infractructure.http

import com.github.torczuk.NoStartEventConsumer
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import com.github.torczuk.domain.EventProducer
import com.github.torczuk.util.HttpTest
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
class BookingControllerTest(
        @Autowired override val restTemplate: TestRestTemplate,
        @LocalServerPort override val serverPort: Int,
        @Autowired val clock: Clock) : HttpTest {

    @MockBean
    private lateinit var repository: BookingEventRepository
    @MockBean
    private lateinit var eventProducer: EventProducer<BookingEvent>

    @Test
    fun `link to POSTed booking should be available in response under location key`() {
        val transactionId = uuid()

        val response = POST("/api/v1/bookings/$transactionId")

        verify(eventProducer).publish(BookingEvent(transactionId, "created", clock.millis()))
        assertThat(response.statusCode).isEqualTo(ACCEPTED)
        assertThat(response.body).isEqualTo("""{"location": "/api/v1/bookings/$transactionId"}""".trimIndent())
    }

    @Test
    fun `status of booking can be check by GET request`() {
        val transaction = uuid()
        val now = Instant.now().toEpochMilli()
        given(repository.findBy(transaction)).willReturn(listOf(BookingEvent(transaction, "created", now)))

        val response = GET("/api/v1/bookings/$transaction")

        assertThat(response.statusCode).isEqualTo(OK)
        assertThat(response.body).isEqualTo("""[{"transaction":"$transaction","type":"created","timestamp":$now}]""".trimIndent())
    }
}