package com.github.torczuk.infractructure.database

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.stream.Collectors.toList

@Repository
class FileBasedBookingEventRepository(path: String) : BookingEventRepository {
    val location = Paths.get(path)

    override fun exist(transactionId: String): Boolean {
        return Files.list(location).anyMatch { file -> file.fileName.toFile().name.startsWith(transactionId) }
    }

    override fun findAll(): List<BookingEvent> {
        return Files.list(location)
                .map { file -> file.fileName.toFile().name.split("_") }
                .map { list -> BookingEvent(list[0], list[1]) }
                .collect(toList())
    }

    override fun findBy(transactionId: String): List<BookingEvent> {
        return Files.list(location)
                .filter { file -> file.fileName.toFile().name.startsWith(transactionId) }
                .map { file -> file.fileName.toFile().name.split("_") }
                .map { list -> BookingEvent(list[0], list[1]) }
                .collect(toList())
    }

    override fun save(event: BookingEvent) {
        Files.write(location.resolve("${event.transaction}_${event.type}"),
                event.toString().toByteArray(Charsets.UTF_8),
                StandardOpenOption.CREATE)
    }
}