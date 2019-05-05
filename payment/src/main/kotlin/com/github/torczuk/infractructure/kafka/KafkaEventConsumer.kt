package com.github.torczuk.infractructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.domain.Event
import com.github.torczuk.domain.OrderEvent
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class KafkaEventConsumer<T : Event>(private val listener: Consumer<T>,
                                    config: ConsumerConfiguration,
                                    private val objectMapper: ObjectMapper,
                                    private val topic: String,
                                    val clazz: Class<out T>) : Runnable {
    val log = LoggerFactory.getLogger(KafkaEventConsumer::class.java)
    val consumer = KafkaConsumer<String, String>(config.properties())

    override fun run() {
        log.info("started listening on {} ... ", topic)
        consumer.subscribe(listOf(topic))
        while (true) {
            val records = consumer.poll(100)
            records.forEach { record ->
                log.info("accepting: {}, key: {}, value: {}", record.offset(), record.key(), record.value())
                val event = objectMapper.readValue(record.value(), clazz);
                listener.accept(event)
            }
        }
    }
}