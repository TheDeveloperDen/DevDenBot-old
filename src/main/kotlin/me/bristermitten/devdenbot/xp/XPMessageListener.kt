package me.bristermitten.devdenbot.xp

import com.google.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bristermitten.devdenbot.data.CachedMessage
import me.bristermitten.devdenbot.data.MessageCache
import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.listener.EventListener
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent
import kotlin.math.roundToInt

/**
 * @author AlexL
 */
@Used
class XPMessageListener @Inject constructor(private val config: DDBConfig) : EventListener {

    companion object {
        private val log by log()
    }

    private suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!shouldCountForStats(event.author, event.message.contentRaw, event.message.channel, config)) {
            return
        }
        val message = event.message
        val member = message.member ?: return

        val strippedMessage = stripMessage(message.contentStripped)

        val gained = xpForMessage(strippedMessage).roundToInt()
        val user = StatsUsers[message.author.idLong]

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
        user.giveXP(gained.toBigInteger())

        checkLevelUp(member, user)

        log.debug {
            "Gave ${event.author.name} $gained XP for a message (${event.message.id})"
        }
    }

    private suspend fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
        val message = event.message

        val user = StatsUsers[message.author.idLong]
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
        user.giveXP(diff.toBigInteger())
        checkLevelUp(member, user)
        log.debug {
            "Adjusted XP of ${member.user.name} by $diff for an edited message (${message.idLong})"
        }
    }

    private suspend fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        val message = MessageCache.getCached(event.messageIdLong) ?: return

        val user = StatsUsers[message.authorId]
        val author = event.jda.retrieveUserById(message.authorId).await() ?: return

        if (!shouldCountForStats(author, message.msg, event.channel, config)) {
            return
        }
        val gained = xpForMessage(message.msg).roundToInt()

        user.giveXP((-gained).toBigInteger())
        log.debug {
            "Took $gained XP from ${author.name} for deleting a message (${message.id})"
        }
    }

    private suspend fun checkLevelUp(member: Member, user: StatsUser) {
        val requiredForNextLevel = xpForLevel(user.level.get() + 1)
        if (user.xp >= requiredForNextLevel) {
            processLevelUp(member, (++user.level).get())
        }
    }

    private suspend fun processLevelUp(user: Member, level: Int) {
        val channel = user.jda.getGuildChannelById(botCommandsChannelId) as? TextChannel ?: return
        channel.sendMessage("${user.asMention}, you levelled up to level **$level**!").await()
        val tier = tierOf(level)
        val tierRole = tierRole(user.jda, tier)
        if (tierRole !in user.roles) {
            if (tier - 1 != 0) { //We can't remove @everyone
                val oldTier = tierRole(user.jda, tier - 1)
                user.guild.removeRoleFromMember(user, oldTier).await()
            }
            user.guild.addRoleToMember(user, tierRole).await()
        }
        log.trace { "Processed level up for ${user.user.name}" }
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageDeleteEvent>().onEach(this::onGuildMessageDelete).launchIn(scope)
        jda.listenFlow<GuildMessageUpdateEvent>().onEach(this::onGuildMessageUpdate).launchIn(scope)
        jda.listenFlow<GuildMessageReceivedEvent>().onEach(this::onGuildMessageReceived).launchIn(scope)
    }
}
