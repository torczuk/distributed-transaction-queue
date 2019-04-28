package com.github.torczuk.domain

import org.slf4j.LoggerFactory

class OrderEventListener(val orderEventRepository: OrderEventRepository) : EventListener<OrderEvent> {
    val log = LoggerFactory.getLogger(OrderEventListener::class.java)

    override fun accept(event: OrderEvent) {
        log.info("processing {} ...", event)
        orderEventRepository.save(event)
    }
}