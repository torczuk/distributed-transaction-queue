package com.github.torczuk.domain

interface BookingEventRepository {

    fun save(event: BookingEvent)

    fun findBy(transactionId: String): BookingEvent?

    fun exist(transactionId: String): Boolean

    fun findAll(): List<BookingEvent>
}