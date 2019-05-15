package com.github.torczuk.infractructure.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

@ConfigurationProperties
class ConsumerConfiguration {
    fun properties(): Properties {
        val props = Properties()
        props.put("bootstrap.servers", bootstrapServer())
        props.put("group.id", "payment-component")
        props.put("enable.auto.commit", "false")
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        return props;
    }

    fun bootstrapServer() = System.getenv("KAFKA_SERVER") ?: "localhost:9092"
}