package net.developerden.devdenbot.xp

import com.google.inject.Inject
import net.developerden.devdenbot.data.CachedMessage
import net.developerden.devdenbot.data.MessageCache
import net.developerden.devdenbot.data.StatsUserDAO
import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.listener.EventListener
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.trait.HasConfig
import net.developerden.devdenbot.util.handleEachIn
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.log
import net.developerden.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
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
class XPMessageListener @Inject constructor(override val ddbConfig: DDBConfig) : EventListener, HasConfig {

    companion object {
        private val log by log()
    }

    private suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!shouldCountForStats(event.author, event.message.contentRaw, event.message.channel, ddbConfig)) {
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
        user.addXP(gained.toLong())

        checkLevelUp(member, user)

        log.debug {
            "Gave ${event.author.name} $gained XP for a message (${event.message.id})"
        }
    }

    private suspend fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
        val message = event.message

        val user = StatsUsers.get(message.author.idLong)
        val member = event.member ?: return

        val prevMessage = MessageCache.getCached(event.messageIdLong) ?: return

        val prevXP = if (shouldCountForStats(event.author, prevMessage.msg, event.channel, ddbConfig))
            xpForMessage(prevMessage.msg)
        else 0.0

        val strippedMessage = stripMessage(event.message.contentRaw)

        val curXP = if (shouldCountForStats(event.author, event.message.contentRaw, event.channel, ddbConfig))
            xpForMessage(strippedMessage)
        else 0.0

        val diff = curXP.roundToInt() - prevXP.roundToInt()

        MessageCache.update(event.messageIdLong, event.message.contentRaw)
        user.addXP(diff.toLong())
        checkLevelUp(member, user)
        log.debug {
            "Adjusted XP of ${member.user.name} by $diff for an edited message (${message.idLong})"
        }
    }

    private suspend fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        val message = MessageCache.getCached(event.messageIdLong) ?: return

        val user = StatsUsers.get(message.authorId)
        val author = event.jda.retrieveUserById(message.authorId).await() ?: return

        if (!shouldCountForStats(author, message.msg, event.channel, ddbConfig)) {
            return
        }
        val gained = xpForMessage(message.msg).roundToInt()

        user.addXP(-gained.toLong())
        log.debug {
            "Took $gained XP from ${author.name} for deleting a message (${message.id})"
        }
    }

    suspend fun checkLevelUp(member: Member, userDAO: StatsUserDAO) = newSuspendedTransaction {
        val requiredForNextLevel = xpForLevel(userDAO.level + 1)
        if (userDAO.xp >= requiredForNextLevel) {
            userDAO.setLevel(userDAO.level + 1)
            processLevelUp(member, userDAO.level)
        } else {
            val currentTier = tierRole(member.jda, tierOf(userDAO.level))
            if (currentTier.isPublicRole) {
                return@newSuspendedTransaction
            }
            member.guild.addRoleToMember(member.idLong, currentTier).await() // just in case they dont already have it
        }
    }


    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageDeleteEvent>().handleEachIn(scope, this::onGuildMessageDelete)
        jda.listenFlow<GuildMessageUpdateEvent>().handleEachIn(scope, this::onGuildMessageUpdate)
        jda.listenFlow<GuildMessageReceivedEvent>().handleEachIn(scope, this::onGuildMessageReceived)
    }
}
