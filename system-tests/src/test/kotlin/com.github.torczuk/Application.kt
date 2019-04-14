package com.github.torczuk

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@SpringBootApplication
class Application {

    fun main(args: Array<String>) {
        val app = SpringApplication(Application::class.java)
        app.webApplicationType = WebApplicationType.NONE
        app.run(*args)
    }

    @Bean
    fun restTemplate() = RestTemplate()

}
