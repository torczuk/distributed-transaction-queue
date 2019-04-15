package com.github.torczuk.domain

interface EventProducer {
    fun publish(bookingEvent: BookingEvent)
}