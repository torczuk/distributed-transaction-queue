package com.github.torczuk.infractructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.EventProducer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory

class KafkaEventProducer(config: ProducerConfiguration,
                         private val topic: String,
                         val objectMapper: ObjectMapper) : EventProducer {

    val producer = KafkaProducer<String, String>(config.properties())
    val log = LoggerFactory.getLogger(KafkaEventProducer::class.java)

    override fun publish(bookingEvent: BookingEvent) {
        producer.send(ProducerRecord(topic, bookingEvent.transaction, objectMapper.writeValueAsString(bookingEvent)))
        log.info("published on {}:  {}", topic, bookingEvent);
    }
}
