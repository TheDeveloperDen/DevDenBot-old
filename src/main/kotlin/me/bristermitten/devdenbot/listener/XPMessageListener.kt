package me.bristermitten.devdenbot.listener

import com.google.inject.Inject
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.log
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.math.log10
import kotlin.math.pow

/**
 * @author AlexL
 */
class XPMessageListener @Inject constructor(private val config: DDBConfig) : ListenerAdapter() {

    private val logger by log()

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.message.contentRaw.startsWith(config.prefix)) {
            return
        }
        if (!event.message.shouldCountForStats()) {
            return
        }

        val message = event.message
        val len = message.contentDisplay.length
        val gained = (3.0 * log10(len.toDouble()).pow(2.6) + +(0..3).random()).toInt()

        val user = StatsUsers[message.author.idLong]
        synchronized(user) {
            user.recentMessages.add(message.contentDisplay)
            user.lastMessageSentTime = System.currentTimeMillis()
            user.xp += gained.toBigInteger()

            logger.info {
                "Gave ${event.author.name} $gained XP for a message (${event.message.id})"
            }
        }
    }
}
