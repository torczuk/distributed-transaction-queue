package com.github.torczuk.infractructure.database

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.rules.TemporaryFolder
import java.util.*

class FileBasedBookingEventRepositoryTest {

    @Rule
    val tempDir = TemporaryFolder()
    lateinit var repository: BookingEventRepository

    @BeforeEach
    internal fun setUp() {
        tempDir.create()
        val newFile = tempDir.newFolder("db")
        repository = FileBasedBookingEventRepository(newFile.absolutePath)
    }

    @Test
    fun `should return false when transaction does not exist`() {
        val nonExistingTransaction = UUID.randomUUID().toString()

        assertThat(repository.exist(nonExistingTransaction)).isFalse()
    }

    @Test
    fun `should return true when transaction exist`() {
        val newTransaction = UUID.randomUUID().toString()

        repository.save(BookingEvent(newTransaction))

        assertThat(repository.exist(newTransaction)).isTrue()
    }

    @Test
    fun `should return empty collection when event can not be found by id`() {
        val event1 = BookingEvent(UUID.randomUUID().toString())
        val event2 = BookingEvent(UUID.randomUUID().toString())
        repository.save(event1)
        repository.save(event2)

        val foundedEvent = repository.findBy(UUID.randomUUID().toString())

        assertThat(foundedEvent).isEmpty()
    }

    @Test
    fun `should find transaction by id`() {
        val transaction = UUID.randomUUID().toString()
        val createdEvent = BookingEvent(transaction)
        val cancelledEvent = BookingEvent(transaction, "canceled")
        repository.save(createdEvent)
        repository.save(cancelledEvent)

        val events = repository.findBy(cancelledEvent.transaction)

        assertThat(events).hasSize(2)
        assertThat(events).contains(createdEvent, cancelledEvent)
    }
}