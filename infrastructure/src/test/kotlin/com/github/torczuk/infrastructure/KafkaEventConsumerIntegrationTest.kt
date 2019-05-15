package com.github.torczuk.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.infrastructure.util.AlwaysProcessingConsumer
import com.github.torczuk.infrastructure.util.AlwaysThrowingExceptionConsumer
import com.github.torczuk.infrastructure.util.Stubs.Companion.uuid
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.awaitility.Awaitility.await
import org.awaitility.Duration.ONE_SECOND
import org.awaitility.Duration.TEN_SECONDS
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import java.util.concurrent.Executors

@SpringBootTest(classes = [Application::class])
internal class KafkaEventConsumerIntegrationTest(
        @Autowired val objectMapper: ObjectMapper
) {

    private val producer = TestKafkaEventProducer()
    private val topic = "test_booking_topic"
    private val alwaysThrowingExceptionConsumer = AlwaysThrowingExceptionConsumer()
    private val alwaysProcessingConsumer = AlwaysProcessingConsumer()
    private val threadPoolExecutor = Executors.newFixedThreadPool(2)

    @Test
    internal fun `should be transactional, means any exception thrown while processing event should move back event to topic`() {
        val event = BookingEvent(uuid())
        val json = objectMapper.writeValueAsString(event)
        producer.publish(topic, json)

        val errorConsumer = KafkaEventConsumer(
                alwaysThrowingExceptionConsumer,
                consumerProperties(),
                objectMapper,
                topic,
                BookingEvent::class.java)
        startConsumer(errorConsumer)

        await("error consumer start consuming events").pollDelay(ONE_SECOND).atMost(TEN_SECONDS).until {
            alwaysThrowingExceptionConsumer.events.contains(event)
        }

        val normalConsumer = KafkaEventConsumer(
                alwaysProcessingConsumer,
                consumerProperties(),
                objectMapper,
                topic,
                BookingEvent::class.java)
        startConsumer(normalConsumer)

        await("normal consumer start consuming events").pollDelay(ONE_SECOND).atMost(TEN_SECONDS).until {
            alwaysProcessingConsumer.events.contains(event)
        }

    }

    private fun startConsumer(kafkaEventConsumer: KafkaEventConsumer<BookingEvent>) {
        threadPoolExecutor.submit(kafkaEventConsumer)
    }

    fun consumerProperties(): Properties {
        val props = Properties()
        props.put("bootstrap.servers", "localhost:9092")
        props.put("group.id", "transactional-consumer")
        props.put("enable.auto.commit", "false")
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        return props;
    }
}