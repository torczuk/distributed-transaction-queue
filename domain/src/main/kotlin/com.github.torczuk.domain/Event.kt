package com.github.torczuk.domain

interface Event {
    val transaction: String
    val type: String
    val timestamp: Long

    fun cancel(timestamp: Long): Event
    fun confirm(timestamp: Long): Event
}