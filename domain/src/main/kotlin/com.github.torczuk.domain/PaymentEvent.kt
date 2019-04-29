package com.github.torczuk.domain

import java.time.Instant

data class PaymentEvent(override val transaction: String,
                        override val type: String = "created",
                        override val timestamp: Long = Instant.now().toEpochMilli()) : Event