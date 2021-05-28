package me.bristermitten.devdenbot.xp

import com.google.inject.Inject
import me.bristermitten.devdenbot.data.CachedMessage
import me.bristermitten.devdenbot.data.MessageCache
import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.listener.ReflectiveEventListener
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.log
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.math.roundToInt

/**
 * @author AlexL
 */
@Used
class XPMessageListener @Inject constructor(private val config: DDBConfig) : ReflectiveEventListener() {

    companion object {
        private val log by log()
    }

    @Listener
    suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!shouldCountForStats(event.author, event.message.contentRaw, event.message.channel, config)) {
            return
        }
        val message = event.message
        val member = message.member ?: return

        val strippedMessage = stripMessage(message.contentStripped)

        val gained = xpForMessage(strippedMessage).roundToInt()
        val user = StatsUsers.get(message.author.idLong)

        if (isTooSimilar(user, strippedMessage)) {
            log.debug {
                "Message $strippedMessage was discarded as it was too similar to previous messages - ${
                    user.recentMessages.associateWith { similarityProportion(it.msg, strippedMessage) }
                }"
            }
            return
        }

        val toCache = CachedMessage(
            message.idLong,
            member.idLong,
            message.timeCreated.toInstant().toEpochMilli(),
            strippedMessage
        )

        user.recentMessages.add(toCache)
        MessageCache.cache(toCache)
        user.lastMessageSentTime = System.currentTimeMillis()
        user.addXP(gained)

        checkLevelUp(member, user)

        log.debug {
            "Gave ${event.author.name} $gained XP for a message (${event.message.id})"
        }
    }

    suspend fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
        val message = event.message

        val user = StatsUsers.get(message.author.idLong)
        val member = event.member ?: return

        val prevMessage = MessageCache.getCached(event.messageIdLong) ?: return

        val prevXP = if (shouldCountForStats(event.author, prevMessage.msg, event.channel, config))
            xpForMessage(prevMessage.msg)
        else 0.0

        val strippedMessage = stripMessage(event.message.contentRaw)

        val curXP = if (shouldCountForStats(event.author, event.message.contentRaw, event.channel, config))
            xpForMessage(strippedMessage)
        else 0.0

        val diff = curXP.roundToInt() - prevXP.roundToInt()

        MessageCache.update(event.messageIdLong, event.message.contentRaw)
        user.addXP(diff)
        checkLevelUp(member, user)
        log.debug {
            "Adjusted XP of ${member.user.name} by $diff for an edited message (${message.idLong})"
        }
    }

    suspend fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        val message = MessageCache.getCached(event.messageIdLong) ?: return

        val user = StatsUsers.get(message.authorId)
        val author = event.jda.retrieveUserById(message.authorId).await() ?: return

        if (!shouldCountForStats(author, message.msg, event.channel, config)) {
            return
        }
        val gained = xpForMessage(message.msg).roundToInt()

        user.addXP(-gained.toLong())
        log.debug {
            "Took $gained XP from ${author.name} for deleting a message (${message.id})"
        }
    }

    private suspend fun checkLevelUp(member: Member, user: StatsUser) = newSuspendedTransaction {
        val requiredForNextLevel = xpForLevel(user.level + 1)
        if (user.xp >= requiredForNextLevel) {
            processLevelUp(member, ++user.level)
        }
    }

}
