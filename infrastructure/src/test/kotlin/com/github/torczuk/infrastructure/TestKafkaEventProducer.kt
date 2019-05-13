package com.github.torczuk.infrastructure

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

class TestKafkaEventProducer {
    private val props = Properties()

    init {
        props["bootstrap.servers"] = "localhost:9092"
        props["acks"] = "all"
        props["retries"] = 100
        props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
    }

    private val producer = KafkaProducer<String, String>(props)

    fun publish(topic: String, value: String) = producer.send(ProducerRecord(topic, value)).get()

}

