package com.github.torczuk.infractructure.database

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.stream.Collectors.toList

@Repository
class FileBasedBookingEventRepository(
        @Value("\${application.file-based-db.path}") path: String,
        @Autowired val mapper: ObjectMapper) : BookingEventRepository {

    val location = Paths.get(path)

    override fun exist(transactionId: String): Boolean {
        return Files.list(location)
                .anyMatch { path -> path.fileName.toFile().name.startsWith(transactionId) }
    }

    override fun findAll(): List<BookingEvent> {
        return Files.list(location)
                .map { path -> Files.readAllBytes(path)  }
                .map { bytes -> String(bytes) }
                .map { payload -> mapper.readValue(payload, BookingEvent::class.java) }
                .collect(toList())
    }

    override fun findBy(transactionId: String): List<BookingEvent> {
        return Files.list(location)
                .filter { file -> file.fileName.toFile().name.startsWith(transactionId) }
                .map { file -> Files.readAllBytes(file)  }
                .map { bytes -> String(bytes) }
                .map { payload -> mapper.readValue(payload, BookingEvent::class.java) }
                .collect(toList())
    }

    override fun save(event: BookingEvent) {
        Files.write(location.resolve("${event.transaction}_${event.type}"),
                mapper.writeValueAsString(event).toByteArray(Charsets.UTF_8),
                StandardOpenOption.CREATE)
    }
}