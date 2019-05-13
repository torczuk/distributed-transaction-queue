package com.github.torczuk

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.torczuk.docker.Docker
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.util.Stubs.Companion.uuid
import org.awaitility.Awaitility.await
import org.awaitility.Duration.ONE_MINUTE
import org.awaitility.Duration.ONE_SECOND
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ThreadLocalRandom

@SpringBootTest(classes = [Application::class])
internal class DistributedTxtSystemTest(
        @Autowired val restTemplate: RestTemplate,
        @Autowired val objectMapper: ObjectMapper,
        @Value("\${system_test_booking.tcp.8080}") val bookingPort: String,
        @Value("\${system_test_booking.host}") val bookingHost: String,
        @Autowired val docker: Docker
) {

    private val log = LoggerFactory.getLogger(DistributedTxtSystemTest::class.java)

    @SystemTest
    fun `distributed transaction should run successfully when all components are up and running`() {
        logContainers()

        val transactionId = uuid()
        val response = POST("http://$bookingHost:$bookingPort/api/v1/transaction/$transactionId")

        await("booking is confirmed").pollDelay(ONE_SECOND).atMost(ONE_MINUTE).until {
            val statuses = GET("http://$bookingHost:$bookingPort/${location(response.body)}")
            log.info("status for {}: {}", transactionId, statuses.body)
            isConfirmed(statuses.body, transactionId)
        }
    }

    @SystemTest
    fun `should book successfully order even when payment component is not available for defined number of time`() {
        docker.pause("system_test_payment")
        logContainers()

        val transactionId = uuid()
        val response = POST("http://$bookingHost:$bookingPort/api/v1/transaction/$transactionId")
        simulateUnavailability("system_test_payment")

        await("booking is confirmed").pollDelay(ONE_SECOND).atMost(ONE_MINUTE).until {
            val statuses = GET("http://$bookingHost:$bookingPort/${location(response.body)}")
            log.info("status for {}: {}", transactionId, statuses.body)
            isConfirmed(statuses.body, transactionId)
        }
    }

    @SystemTest
    fun `should book successfully order even when inventory component is not available for defined number of time`() {
        docker.pause("system_test_order")
        logContainers()

        val transactionId = uuid()
        val response = POST("http://$bookingHost:$bookingPort/api/v1/transaction/$transactionId")
        simulateUnavailability("system_test_order")

        await("booking is confirmed").pollDelay(ONE_SECOND).atMost(ONE_MINUTE).until {
            val statuses = GET("http://$bookingHost:$bookingPort/${location(response.body)}")
            log.info("status for {}: {}", transactionId, statuses.body)
            isConfirmed(statuses.body, transactionId)
        }
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

    private fun isConfirmed(body: String?, transactionId: String): Boolean {
        val bookings: List<BookingEvent> = objectMapper.readValue(body!!)
        return bookings.filter { event -> event.transaction == transactionId }
                .filter { event -> event.type == "confirmed" }
                .any()
    }

    private fun logContainers() {
        docker.containers().forEach { container ->
            log.info("{}: status: {}", container.names.first(), container.status, container.state)
        }
    }

    private fun simulateUnavailability(component: String) {
        val sleepInterval = ThreadLocalRandom.current().nextLong(10, 60)
        log.info("simulating payment unavailability for $sleepInterval sec ..")
        Thread.sleep(sleepInterval * 1000)
        docker.unpause(component)
        log.info("continue ... ")
    }
}


