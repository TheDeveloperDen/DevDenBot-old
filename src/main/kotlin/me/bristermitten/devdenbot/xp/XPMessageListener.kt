package me.bristermitten.devdenbot.xp

import com.google.inject.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.bristermitten.devdenbot.data.CachedMessage
import me.bristermitten.devdenbot.data.MessageCache
import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.stats.GlobalStats
import me.bristermitten.devdenbot.util.botCommandsChannelId
import me.bristermitten.devdenbot.util.log
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.math.roundToInt

/**
 * @author AlexL
 */
class XPMessageListener @Inject constructor(private val config: DDBConfig) : ListenerAdapter() {

    private val logger by log()

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!shouldCountForStats(event.author, event.message.contentRaw, event.message.channel, config)) {
            return
        }
        val message = event.message
        val member = message.member ?: return

        val strippedMessage = stripMessage(message.contentStripped)

        val gained = xpForMessage(strippedMessage).roundToInt()
        val user = StatsUsers[message.author.idLong]

        val toCache = CachedMessage(
                message.idLong,
                member.idLong,
                message.timeCreated.toInstant().toEpochMilli(),
                strippedMessage
        )

        synchronized(user) {
            user.recentMessages.add(toCache)
            MessageCache.cache(toCache)
            user.lastMessageSentTime = System.currentTimeMillis()
            user.giveXP(gained.toBigInteger())

            checkLevelUp(member, user)

            logger.info {
                "Gave ${event.author.name} $gained XP for a message (${event.message.id})"
            }
        }
    }

    override fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
        val message = event.message
        val contents = stripMessage(event.message.contentRaw)

        val user = StatsUsers[message.author.idLong]
        val member = event.member ?: return

        val prevMessage = MessageCache.getCached(event.messageIdLong) ?: return

        val prevXP = if (shouldCountForStats(event.author, prevMessage.msg, event.channel, config))
            xpForMessage(prevMessage.msg)
        else 0.0

        val curXP = if (shouldCountForStats(event.author, event.message.contentRaw, event.channel, config))
            xpForMessage(contents)
        else 0.0

        val diff = curXP.roundToInt() - prevXP.roundToInt()

        MessageCache.update(event.messageIdLong, event.message.contentRaw)
        synchronized (user) {
            user.giveXP(diff.toBigInteger())
            checkLevelUp(member, user)
            logger.info {
                "Adjusted XP of ${member.user.name} by $diff for an edited message (${message.idLong})"
            }
        }
    }

    override fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        logger.info { MessageCache.getCached(event.messageIdLong) }
        val message = MessageCache.getCached(event.messageIdLong) ?: return
        val contents = stripMessage(message.msg)

        val user = StatsUsers[message.authorId]
        val author = event.jda.getUserById(message.authorId) ?: return

        if (!shouldCountForStats(author, message.msg, event.channel, config)) {
            return
        }

        val gained = xpForMessage(contents).roundToInt()

        synchronized (user) {
            user.giveXP((-gained).toBigInteger())
            logger.info {
                "Took ${-gained} XP from ${author.name} for a message (${message.id})"
            }
        }
    }

    private fun checkLevelUp(member: Member, user: StatsUser) {
        val requiredForNextLevel = xpForLevel(user.level + 1)
        if (user.xp >= requiredForNextLevel) {
            GlobalScope.launch {
                GlobalStats.levelUps++
                sendLevelUpMessage(member, ++user.level)
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
