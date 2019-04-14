package com.github.torczuk.domain

import java.time.Instant

data class BookingEvent(val transaction: String,
                        val type: String = "created",
                        val timestamp: Long = Instant.now().toEpochMilli())