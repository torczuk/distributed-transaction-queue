package com.github.torczuk.domain

import org.slf4j.LoggerFactory

class BookingEventListener : EventListener {
    private val log = LoggerFactory.getLogger(BookingEventListener::class.java)

    override fun accept(bookingEvent: BookingEvent) {
        log.info("processing {} ...", bookingEvent)
    }
}