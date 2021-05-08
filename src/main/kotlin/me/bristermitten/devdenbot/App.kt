package me.bristermitten.devdenbot

import mu.KotlinLogging
import kotlin.system.measureTimeMillis

fun main() {
    val log = KotlinLogging.logger("App")

    log.info("Bot starting!")
    val time = measureTimeMillis {
        DevDen().start()
    }

    log.info { "Bot started in ${time}ms!" }
}
