package com.github.torczuk.util

import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Stubs {
    companion object {
        fun uuid() = UUID.randomUUID().toString()
        fun id() = ThreadLocalRandom.current().nextLong()
    }
}