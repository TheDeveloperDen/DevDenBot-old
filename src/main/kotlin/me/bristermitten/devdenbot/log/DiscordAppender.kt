package me.bristermitten.devdenbot.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import ch.qos.logback.core.encoder.Encoder
import net.dv8tion.jda.api.JDA

class DiscordAppender(private val jda: JDA, private val channelId: Long, var encoder: Encoder<ILoggingEvent>) :
    AppenderBase<ILoggingEvent>() {

    override fun append(eventObject: ILoggingEvent) {
        val channel = jda.getTextChannelById(channelId) ?: return
        val formatted = encoder.encode(eventObject)
        channel.sendMessage(formatted.decodeToString()).queue()
    }


}
