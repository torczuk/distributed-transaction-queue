package com.github.torczuk.infractructure.http

import com.github.torczuk.NoStartEventConsumer
import com.github.torczuk.domain.PaymentEvent
import com.github.torczuk.domain.PaymentEventRepository
import com.github.torczuk.util.HttpTest
import com.github.torczuk.util.Stubs.Companion.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus.OK
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [NoStartEventConsumer::class])
class PaymentControllerTest(
        @Autowired override val restTemplate: TestRestTemplate,
        @LocalServerPort override val serverPort: Int) : HttpTest {

    @MockBean
    private lateinit var repository: PaymentEventRepository

    @Test
    fun shouldResponseTransactionStatus() {
        val transaction = uuid()
        val now = Instant.now().toEpochMilli()
        given(repository.findBy(transaction)).willReturn(listOf(PaymentEvent(transaction, "created", now)))

        val response = GET("/api/v1/payments/$transaction")

        assertThat(response.statusCode).isEqualTo(OK)
        assertThat(response.body).isEqualTo("""[{"transaction":"$transaction","type":"created","timestamp":$now}]""".trimIndent())
    }
}