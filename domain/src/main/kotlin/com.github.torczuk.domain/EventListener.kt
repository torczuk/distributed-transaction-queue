package com.github.torczuk.domain

import java.util.function.Consumer

interface EventListener<T: Event>: Consumer<T> {
}