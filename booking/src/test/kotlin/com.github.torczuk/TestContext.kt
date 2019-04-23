package com.github.torczuk

import com.github.torczuk.domain.BookingEventRepository
import com.github.torczuk.util.InMemoryBookingEventRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestContext {
    @Bean
    @Primary
    fun bookingEventRepository(): BookingEventRepository {
        return InMemoryBookingEventRepository()
    }
}