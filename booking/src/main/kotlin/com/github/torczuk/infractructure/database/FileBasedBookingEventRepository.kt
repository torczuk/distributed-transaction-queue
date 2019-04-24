package com.github.torczuk.infractructure.database

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

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
        return Files.list(location)
                .filter { file -> file.fileName.toFile().name == transactionId }
                .findFirst()
                .map { file -> BookingEvent(transactionId) }
                .orElse(null)
    }

    override fun save(event: BookingEvent) {
        Files.write(location.resolve(event.transaction),
                event.toString().toByteArray(Charsets.UTF_8),
                StandardOpenOption.CREATE)
    }

}