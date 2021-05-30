package me.bristermitten.devdenbot.events

import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.listener.EventListener
import me.bristermitten.devdenbot.util.handleEachIn
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

@Used
class JoinLeaveListener : EventListener {

    private suspend fun onGuildJoin(event: GuildMemberJoinEvent) = newSuspendedTransaction {
        JoinLeaves.insert {
            it[timestamp] = LocalDateTime.now()
            it[uid] = event.user.idLong
            it[username] = "${event.user.name}#${event.user.discriminator}"
            it[type] = JoinLeaves.Type.JOIN
        }
    }

    private suspend fun onGuildLeave(event: GuildMemberRemoveEvent) = newSuspendedTransaction {
        JoinLeaves.insert {
            it[timestamp] = LocalDateTime.now()
            it[uid] = event.user.idLong
            it[username] = "${event.user.name}#${event.user.discriminator}"
            it[type] = JoinLeaves.Type.LEAVE
        }
    }


    override fun register(jda: JDA) {
        jda.listenFlow<GuildMemberJoinEvent>()
            .handleEachIn(scope, this::onGuildJoin)

        jda.listenFlow<GuildMemberRemoveEvent>()
            .handleEachIn(scope, this::onGuildLeave)
    }
}
