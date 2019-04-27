package com.github.torczuk

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.file.Paths

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val app = runApplication<Application>(*args)
    val dbPath = app.environment.getProperty("application.file-based-db.path")
    Paths.get(dbPath).toFile().mkdirs()
}

