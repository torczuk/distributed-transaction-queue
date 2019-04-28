package com.github.torczuk

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.torczuk.domain.BookingEvent
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.awaitility.Duration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.*

@SpringBootTest(classes = [Application::class])
internal class DistributedTxtSystemTest(
        @Autowired val restTemplate: RestTemplate,
        @Autowired val objectMapper: ObjectMapper,
        @Value("\${system_test_booking.tcp.8080}") val bookingPort: String,
        @Value("\${system_test_booking.host}") val bookingHost: String
) {

    private val log = LoggerFactory.getLogger(DistributedTxtSystemTest::class.java)

    @SystemTest
    fun `should book successfully order when both components are are available`() {
        //given all components up and running
        //TODO check status

        //when
        val transaction = UUID.randomUUID().toString()
        val response = POST("http://$bookingHost:$bookingPort/api/v1/transaction/$transaction")


        Awaitility.await("posted transaction is available").pollDelay(Duration.ONE_SECOND).atMost(Duration.ONE_MINUTE).until {
            val statuses = GET("http://$bookingHost:$bookingPort/${location(response.body)}")
            log.info("status for {}: {}", transaction, statuses.body)
            containsTransaction(statuses.body, transaction)
        }
    }

    @SystemTest
    fun `should book successfully order even when payment component is not available for defined number of time`() {
        //given all components but payment up and running
        //TODO check status

        //when
        val transaction = UUID.randomUUID().toString()
        POST("http://$bookingHost:$bookingPort/api/v1/transaction/$transaction")
        //and
        //payment component is up


        //then
        //TODO add avaitility
        val response = GET("http://$bookingHost:$bookingPort/api/v1/transaction/$transaction")
        assertThat(response.body).isEqualTo("""{"status": "success"}""".trimIndent())
    }

    @SystemTest
    fun `should book successfully order even when inventory component is not available for defined number of time`() {
        //given all components but storage up and running
        //TODO check status

        //when
        val transaction = UUID.randomUUID().toString()
        POST("http://$bookingHost:$bookingPort/api/v1/transaction/$transaction")
        //and
        //storage component is up


        //then
        //TODO add avaitility
        val response = GET("http://$bookingHost:$bookingPort/api/v1/transaction/$transaction")
        assertThat(response.body).isEqualTo("""{"status": "success"}""".trimIndent())
    }

    private fun POST(url: String): ResponseEntity<String> {
        log.info("POST: $url")
        return restTemplate.postForEntity<String>(
                url, null, String::class.java)
    }

    private fun GET(url: String): ResponseEntity<String> {
        log.info("GET: $url")
        return restTemplate.getForEntity(url, String::class.java)
    }

    private fun location(body: String?) = objectMapper.readValue<Map<String, String>>(body!!)["location"]!!

    private fun containsTransaction(body: String?, transactionId: String): Boolean {
        val bookings: List<BookingEvent> = objectMapper.readValue(body!!)
        return bookings.filter { event -> event.transaction == transactionId }
                .filter {event -> event.type == "success" }
                .any()
    }
}


