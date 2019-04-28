package com.github.torczuk.domain

interface EventProducer<in T : Event> {
    fun publish(event: T)
}