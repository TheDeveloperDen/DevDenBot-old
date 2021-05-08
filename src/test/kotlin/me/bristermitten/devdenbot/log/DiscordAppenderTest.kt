package me.bristermitten.devdenbot.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.layout.TTLLLayout
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import me.bristermitten.devdenbot.mock.MockMessageAction
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.internal.entities.DataMessage
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class DiscordAppenderTest {

    @Test
    fun `Test Basic DiscordAppender Logging`() {
        val logger = LoggerFactory.getILoggerFactory().getLogger("tests") as Logger
        val messages = mutableListOf<String>()
        val context = LoggerFactory.getILoggerFactory() as LoggerContext

        val mockedChannel = mock<TextChannel> {
            on { it.sendMessage(any<String>()) }.then {
                messages += (it.arguments[0] as String)
                MockMessageAction(DataMessage(false, null, null, null))
            }
        }
        val jda = mock<JDA> {
            on { it.getTextChannelById(any<Long>()) }.thenReturn(mockedChannel)
        }
        val ttllLayout = TTLLLayout().apply {
            this.context = context
            start()
        }
        val encoder = LayoutWrappingEncoder<ILoggingEvent>().apply {
            this.context = context
            layout = ttllLayout
            start()
        }
        val appender = DiscordAppender(jda, 1L, encoder)

        appender.context = context
        appender.start()
        logger.isAdditive = false
        logger.addAppender(appender)

        val message = "HELLO"
        logger.info(message)
        logger.level = Level.INFO
        logger.debug("haha")
        assertEquals(1, messages.size)
        assertTrue {
            messages.first().contains(message) //Can't really replicate the actual output without a mocked ILoggingEvent
        }
    }
}
