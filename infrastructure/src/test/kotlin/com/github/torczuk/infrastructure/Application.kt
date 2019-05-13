package com.github.torczuk.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@SpringBootApplication
class Application {

    fun main(args: Array<String>) {
        val app = SpringApplication(Application::class.java)
        app.webApplicationType = WebApplicationType.NONE
        app.run(*args)
    }

    @Bean
    fun objecMapper() {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(KotlinModule())
    }
}
