package com.github.torczuk.domain

interface Publisher {
    fun publish(bookingEvent: BookingEvent)
}