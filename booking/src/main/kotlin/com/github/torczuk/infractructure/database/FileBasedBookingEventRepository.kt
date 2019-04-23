package com.github.torczuk.infractructure.database

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Paths

@Repository
class FileBasedBookingEventRepository(path: String) : BookingEventRepository {
    val location = Paths.get(path)

    override fun exist(transactionId: String): Boolean {
        return Files.list(location).anyMatch { file -> file.fileName.toFile().name == transactionId }
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