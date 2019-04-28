package com.github.torczuk.infractructure.http

import com.github.torczuk.domain.OrderEvent
import com.github.torczuk.domain.OrderEventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["api/v1/orders"])
class OrderController(@Autowired val orderEventRepository: OrderEventRepository) {

    @GetMapping(path = ["/{id}"])
    fun transactionStatus(@PathVariable("id") transactionId: String): ResponseEntity<List<OrderEvent>> {
        val events = orderEventRepository.findBy(transactionId)
        return ResponseEntity.ok(events)
    }
}
