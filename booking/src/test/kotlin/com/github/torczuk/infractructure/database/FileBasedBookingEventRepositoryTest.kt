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
    fun `should return null when event can not be found by id`() {
        val event1 = BookingEvent(UUID.randomUUID().toString())
        val event2 = BookingEvent(UUID.randomUUID().toString())
        repository.save(event1)
        repository.save(event2)

        val foundedEvent = repository.findBy(UUID.randomUUID().toString())

        assertThat(foundedEvent).isNull()
    }

    @Test
    fun `should find transaction by id`() {
        val event1 = BookingEvent(UUID.randomUUID().toString())
        val event2 = BookingEvent(UUID.randomUUID().toString(), "canceled")
        repository.save(event1)
        repository.save(event2)

        val foundedEvent = repository.findBy(event2.transaction)

        assertThat(foundedEvent).isEqualTo(event2)
    }
}