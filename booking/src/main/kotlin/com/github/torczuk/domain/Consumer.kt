package com.github.torczuk.domain

interface Consumer {
    fun get(): BookingEvent
}