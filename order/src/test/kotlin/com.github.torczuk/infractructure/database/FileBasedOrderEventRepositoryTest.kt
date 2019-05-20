package com.github.torczuk.infractructure.database

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.torczuk.domain.OrderEvent
import com.github.torczuk.domain.OrderEventRepository
import com.github.torczuk.util.Stubs.Companion.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.rules.TemporaryFolder

class FileBasedOrderEventRepositoryTest {

    @Rule
    private val tempDir = TemporaryFolder()
    private val mapper = ObjectMapper().registerModule(KotlinModule())
    private lateinit var repository: OrderEventRepository
    private val dbFolderName = "db"

    @BeforeEach
    internal fun setUp() {
        tempDir.create()
        val newFile = tempDir.newFolder(dbFolderName)
        repository = FileBasedOrderEventRepository(newFile.absolutePath, mapper)
    }

    @Test
    fun `should return false when transaction does not exist`() {
        val nonExistingTransaction = uuid()

        assertThat(repository.exist(nonExistingTransaction)).isFalse()
    }

    @Test
    fun `should return true when transaction exist`() {
        val newTransaction = uuid()

        repository.save(OrderEvent(newTransaction))

        assertThat(repository.exist(newTransaction)).isTrue()
    }

    @Test
    fun `should return empty collection when event can not be found by id`() {
        val event1 = OrderEvent(uuid())
        val event2 = OrderEvent(uuid())
        repository.save(event1)
        repository.save(event2)

        val foundedEvent = repository.findBy(uuid())

        assertThat(foundedEvent).isEmpty()
    }

    @Test
    fun `should find event by transaction id`() {
        val transaction = uuid()
        val createdEvent = OrderEvent(transaction)
        val cancelledEvent = OrderEvent(transaction, "cancelled")
        repository.save(createdEvent)
        repository.save(cancelledEvent)

        val events = repository.findBy(cancelledEvent.transaction)

        assertThat(events).hasSize(2)
        assertThat(events).contains(createdEvent, cancelledEvent)
    }

    @Test
    fun `should find all events`() {
        val transaction = uuid()
        val createdEvent = OrderEvent(transaction)
        val cancelledEvent = OrderEvent(transaction, "cancelled")
        val otherEvent = OrderEvent(uuid())
        repository.save(createdEvent)
        repository.save(cancelledEvent)
        repository.save(otherEvent)

        val events = repository.findAll()

        assertThat(events).hasSize(3)
        assertThat(events).contains(createdEvent, cancelledEvent, otherEvent)
    }

    @Test
    fun `save should be idempotent`() {
        val event = OrderEvent(uuid())
        repository.save(event)
        repository.save(event)

        val events = repository.findAll()

        assertThat(events).hasSize(1)
        assertThat(events).contains(event)
    }
}