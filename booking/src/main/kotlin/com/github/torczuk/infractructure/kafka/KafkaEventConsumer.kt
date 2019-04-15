package com.github.torczuk.infractructure.kafka

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.EventListener
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.function.Consumer

class KafkaEventConsumer(private val listener: Consumer<BookingEvent>,
                         config: ConsumerConfiguration,
                         private val topic: String) : Runnable {

    val log = LoggerFactory.getLogger(KafkaEventProducer::class.java)
    val consumer = KafkaConsumer<String, String>(config.properties())

    override fun run() {
        consumer.subscribe(listOf(topic))
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            records.forEach { record ->
                log.info("offset: {}, key: {}, value: {}", record.offset(), record.key(), record.value())
                listener.accept(BookingEvent(""))
            }
        }
    }
}