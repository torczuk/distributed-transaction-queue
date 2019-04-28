package com.github.torczuk.domain

import java.util.function.Consumer

interface EventListener: Consumer<BookingEvent> {
}