package me.bristermitten.devdenbot.events

import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.listener.EventListener
import me.bristermitten.devdenbot.util.handleEachIn
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@Used
class EventsListener : EventListener {

    private suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        newSuspendedTransaction {
            Events.insert {
                it[action] = EventType.USER_MESSAGE
                it[timestamp] = event.message.timeCreated.toLocalDateTime()
            }
        }
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>()
            .handleEachIn(scope, this::onGuildMessageReceived)
    }
}
