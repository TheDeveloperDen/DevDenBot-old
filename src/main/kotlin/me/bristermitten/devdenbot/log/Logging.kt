package me.bristermitten.devdenbot.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.layout.TTLLLayout
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import net.dv8tion.jda.api.JDA
import org.slf4j.LoggerFactory


private var init = false


private val rootLogger = LoggerFactory.getILoggerFactory().getLogger(Logger.ROOT_LOGGER_NAME) as Logger
fun initLogging(jda: JDA, channelId: Long) {
    check(!init) {
        "Already initialised"
    }
    setLoggingLevel(Level.INFO)
    val context = LoggerFactory.getILoggerFactory() as LoggerContext


    val ttllLayout = TTLLLayout().apply {
        this.context = context
        start()
    }
    val encoder = LayoutWrappingEncoder<ILoggingEvent>().apply {
        this.context = context
        layout = ttllLayout
        start()
    }
    val appender = DiscordAppender(jda, channelId, encoder)
    appender.context = context
    appender.start()

    rootLogger.addAppender(appender)
}

fun setLoggingLevel(level: Level) {
    val context = LoggerFactory.getILoggerFactory() as LoggerContext
    context.loggerList.forEach {
        it.level = level
    }
}
