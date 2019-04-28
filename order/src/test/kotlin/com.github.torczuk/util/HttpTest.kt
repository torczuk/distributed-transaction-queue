package com.github.torczuk.util

import org.springframework.boot.test.web.client.TestRestTemplate

interface HttpTest {
    val restTemplate: TestRestTemplate
    val serverPort: Int

    fun POST(uri: String) = restTemplate.postForEntity<String>(
            "http://localhost:$serverPort/$uri",
            null,
            String::class.java)

    fun GET(uri: String) = restTemplate.getForEntity(
            "http://localhost:$serverPort/$uri",
            String::class.java)
}