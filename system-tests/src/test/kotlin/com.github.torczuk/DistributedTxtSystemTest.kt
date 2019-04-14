package com.github.torczuk

import org.assertj.core.api.Assertions.assertThat
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

        @Value("\${booking_test.tcp.8080}") val bookingPort: String,
        @Value("\${booking_test.host}") val bookingHost: String
) {

    private val log = LoggerFactory.getLogger(DistributedTxtSystemTest::class.java)

    @SystemTest
    fun `should book successfully order when both components are are available`() {
        //given all components up and running
        //TODO check status

        //when
        val transaction = UUID.randomUUID().toString()
        POST("http://$bookingHost:$bookingPort/api/v1/transaction/$transaction")

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
}


