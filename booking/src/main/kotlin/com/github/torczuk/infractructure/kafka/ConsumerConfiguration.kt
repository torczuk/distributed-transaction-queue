package com.github.torczuk.infractructure.kafka

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

@ConfigurationProperties
class ConsumerConfiguration {
    fun properties(): Properties {
        val props = Properties()
        props.put("bootstrap.servers", "localhost:9092")
        props.put("group.id", "test")
        props.put("enable.auto.commit", "true")
        props.put("auto.commit.interval.ms", "1000")
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        return props;
    }
}