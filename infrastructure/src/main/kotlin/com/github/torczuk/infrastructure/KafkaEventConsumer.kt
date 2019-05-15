package com.github.torczuk.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.domain.Event
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Consumer

class KafkaEventConsumer<T : Event>(private val listener: Consumer<T>,
                                    consumerProperties: Properties,
                                    private val objectMapper: ObjectMapper,
                                    private val topic: String,
                                    val clazz: Class<out T>,
                                    val consumer: KafkaConsumer<String, String> = KafkaConsumer(consumerProperties)) : Runnable {

    val log = LoggerFactory.getLogger(KafkaEventConsumer::class.java)

    override fun run() {
        log.info("started listening on {} ... ", topic)
        consumer.subscribe(listOf(topic))
        try {
            while (true) {
                val records = consumer.poll(100)
                records.forEach { record ->
                    log.info("accepting: {}, key: {}, value: {}", record.offset(), record.key(), record.value())
                    val event = objectMapper.readValue(record.value(), clazz);
                    listener.accept(event)
                    consumer.commitSync()
                }
            }
        } catch (e: RuntimeException) {
            log.error("closing consumer on: ${topic}", e)
            consumer.close()
        }
    }
}
