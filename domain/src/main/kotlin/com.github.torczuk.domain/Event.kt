package com.github.torczuk.domain

interface Event {
    val transaction: String
    val type: String
    val timestamp: Long
}