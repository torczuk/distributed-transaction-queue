package com.github.torczuk.infractructure.kafka

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

@ConfigurationProperties
class ProducerConfiguration {
    fun properties(): Properties {
        val props = Properties()
        props["bootstrap.servers"] = "localhost:9092"
        props["acks"] = "all"
        props["retries"] = 100
        props["batch.size"] = 16384
        props["linger.ms"] = 1
        props["buffer.memory"] = 33554432
        props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        return props
    }

}