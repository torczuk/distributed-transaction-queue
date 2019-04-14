package com.github.torczuk

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@kotlin.annotation.Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention
@Tag("system")
@Test
annotation class SystemTest
