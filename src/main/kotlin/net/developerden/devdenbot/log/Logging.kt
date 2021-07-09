package net.developerden.devdenbot.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory

fun setLoggingLevel(level: Level) {
    val context = LoggerFactory.getILoggerFactory() as LoggerContext
    context.loggerList.forEach {
        it.level = level
    }
}
