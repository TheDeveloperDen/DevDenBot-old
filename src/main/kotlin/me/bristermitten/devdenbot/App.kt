package me.bristermitten.devdenbot

import kotlin.system.measureTimeMillis


fun main() {
    println("Bot starting!")

    val time = measureTimeMillis {
        DevDen().start()
    }
    println("Bot started in ${time}ms!")
}
