package com.github.torczuk.infractructure.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["api/v1/transaction"])
class TransactionController {

    @PostMapping(path = ["/{id}"])
    fun processTransaction(@PathVariable("id") transactionId: String): ResponseEntity<String> {
        val location = """{"location": "/api/v1/transaction/$transactionId"}"""
        return ResponseEntity.accepted().body(location)
    }

    @GetMapping(path = ["/{id}"])
    fun transactionStatus(@PathVariable("id") transactionId: String): ResponseEntity<String> {
        val status = """{"status": "in progress"}"""
        return ResponseEntity.ok(status)
    }
}
