package me.bristermitten.devdenbot.listener

import com.google.inject.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.botCommandsChannelId
import me.bristermitten.devdenbot.util.log
import me.bristermitten.devdenbot.xp.tierOf
import me.bristermitten.devdenbot.xp.tierRole
import me.bristermitten.devdenbot.xp.xpForLevel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
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
        val member = message.member ?: return
        val len = message.contentDisplay.length
        val gained = (3.0 * log10(len.toDouble()).pow(2.6) + (0..3).random()).toInt()
        val user = StatsUsers[message.author.idLong]
        synchronized(user) {
            user.recentMessages.add(message.contentDisplay)
            user.lastMessageSentTime = System.currentTimeMillis()
            user.xp += gained.toBigInteger()
            val requiredForNextLevel = xpForLevel(user.level + 1)
            if (user.xp >= requiredForNextLevel) {
                GlobalScope.launch {
                    sendLevelUpMessage(member, ++user.level)
                }
            }

            logger.info {
                "Gave ${event.author.name} $gained XP for a message (${event.message.id})"
            }
        }
    }

    private suspend fun sendLevelUpMessage(user: Member, level: Int) {
        val channel = user.jda.getGuildChannelById(botCommandsChannelId) as? TextChannel ?: return
        channel.sendMessage("${user.asMention}, you levelled up to level **$level**!").await()
        val tier = tierOf(level)
        if (tier != tierOf(level - 1)) {
            val newTier = tierRole(user.jda, tier)
            if (tier - 1 != 0) { //We can't remove @everyone
                val oldTier = tierRole(user.jda, tier - 1)
                user.guild.removeRoleFromMember(user, oldTier).await()
            }
            user.guild.addRoleToMember(user, newTier).await()
        }
    }
}
