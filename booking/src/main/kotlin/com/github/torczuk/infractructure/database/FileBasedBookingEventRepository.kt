package com.github.torczuk.infractructure.database

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import org.springframework.stereotype.Repository

@Repository
class FileBasedBookingEventRepository: BookingEventRepository {

    override fun exist(transactionId: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAll(): List<BookingEvent> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findBy(transactionId: String): BookingEvent? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(event: BookingEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}