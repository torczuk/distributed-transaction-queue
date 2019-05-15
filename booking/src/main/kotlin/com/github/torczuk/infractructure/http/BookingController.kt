package com.github.torczuk.infractructure.http

import com.github.torczuk.domain.BookingEvent
import com.github.torczuk.domain.BookingEventRepository
import com.github.torczuk.domain.EventProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Clock

@RestController
@RequestMapping(path = ["api/v1/bookings"])
class BookingController(@Autowired val bookingEventRepository: BookingEventRepository,
                        @Autowired val eventProducer: EventProducer<BookingEvent>,
                        @Autowired val clock: Clock) {

    @PostMapping(path = ["/{id}"])
    fun processTransaction(@PathVariable("id") transactionId: String): ResponseEntity<String> {
        val location = """{"location": "/api/v1/bookings/$transactionId"}"""
        eventProducer.publish(BookingEvent(transaction = transactionId, timestamp = clock.millis()))
        return ResponseEntity.accepted().body(location)
    }

    @GetMapping(path = ["/{id}"])
    fun transactionStatus(@PathVariable("id") transactionId: String): ResponseEntity<List<BookingEvent>> {
        val events = bookingEventRepository.findBy(transactionId)
        return ResponseEntity.ok(events)
    }
}
