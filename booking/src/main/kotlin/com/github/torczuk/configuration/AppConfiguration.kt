package com.github.torczuk.configuration

import com.github.torczuk.domain.EventProducer
import com.github.torczuk.infractructure.kafka.KafkaEventProducer
import com.github.torczuk.infractructure.kafka.ProducerConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration {

    @Bean
    fun eventProducer(): EventProducer = KafkaEventProducer(ProducerConfiguration(), "booking_events")

}