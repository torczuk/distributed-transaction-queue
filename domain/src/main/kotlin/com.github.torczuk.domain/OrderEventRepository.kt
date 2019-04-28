package com.github.torczuk.domain

interface OrderEventRepository {

    fun save(event: OrderEvent)

    fun findBy(transactionId: String): List<OrderEvent>

    fun exist(transactionId: String): Boolean

    fun findAll(): List<OrderEvent>
}