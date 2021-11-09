package net.developerden.devdenbot.events

import net.developerden.devdenbot.data.MessageCache
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.listener.EventListener
import net.developerden.devdenbot.util.handleEachIn
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.log
import net.developerden.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

@Used
class MessagesListener : EventListener {
    private val log by log()

    private suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        newSuspendedTransaction {
            Messages.insert {
                it[uid] = event.author.idLong
                it[channelId] = event.channel.idLong
                it[timestamp] = event.message.timeCreated.toLocalDateTime()
                it[content] = event.message.contentRaw
                it[type] = Messages.Type.CREATE
            }
        }
    }

    private suspend fun onGuildMessageEdit(event: GuildMessageUpdateEvent) {
        newSuspendedTransaction {
            Messages.insert {
                it[uid] = event.author.idLong
                it[channelId] = event.channel.idLong
                it[timestamp] = event.message.timeEdited?.toLocalDateTime() ?: LocalDateTime.now()
                it[content] = event.message.contentRaw
                it[type] = Messages.Type.EDIT
            }
        }
    }

    private suspend fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        newSuspendedTransaction {
            val message = MessageCache.getCached(event.messageIdLong) ?: run {
                log.warn { "Could not retrieve deleted message from message cache (${event.messageIdLong}" }
                return@newSuspendedTransaction
            }
            Messages.insert {
                it[uid] = message.authorId
                it[channelId] = event.channel.idLong
                it[timestamp] = LocalDateTime.now()
                it[content] = message.msg
                it[type] = Messages.Type.DELETE
            }
        }
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>()
            .handleEachIn(this::onGuildMessageReceived)

        jda.listenFlow<GuildMessageUpdateEvent>()
            .handleEachIn(this::onGuildMessageEdit)

        jda.listenFlow<GuildMessageDeleteEvent>()
            .handleEachIn(this::onGuildMessageDelete)
    }
}
