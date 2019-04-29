package com.github.torczuk.infractructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.domain.Event
import com.github.torczuk.domain.EventProducer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory

class KafkaEventProducer<in T : Event>(config: ProducerConfiguration,
                                       private val topic: String,
                                       val objectMapper: ObjectMapper) : EventProducer<T> {

    val producer = KafkaProducer<String, String>(config.properties())
    val log = LoggerFactory.getLogger(KafkaEventProducer::class.java)

    override fun publish(event: T) {
        producer.send(ProducerRecord(topic, event.transaction, objectMapper.writeValueAsString(event)))
        log.info("published on {}:  {}", topic, event);
    }
}
