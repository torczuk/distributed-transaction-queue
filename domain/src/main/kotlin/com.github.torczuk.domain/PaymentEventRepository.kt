package com.github.torczuk.domain

interface PaymentEventRepository {

    fun save(event: PaymentEvent)

    fun findBy(transactionId: String): List<PaymentEvent>

    fun exist(transactionId: String): Boolean

    fun findAll(): List<PaymentEvent>
}