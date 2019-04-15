package com.github.torczuk.infractructure.kafka

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.EventProducer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component

class KafkaEventProducer(config: ProducerConfiguration,
                         private val topic: String) : EventProducer {

    val producer = KafkaProducer<String, String>(config.properties())

    override fun publish(bookingEvent: BookingEvent) {
        producer.send(ProducerRecord(topic, bookingEvent.toString()))
    }
}
