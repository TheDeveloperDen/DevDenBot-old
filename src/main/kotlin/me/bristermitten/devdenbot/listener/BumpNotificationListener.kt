package me.bristermitten.devdenbot.listener

import club.minnced.jda.reactor.onMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import me.bristermitten.devdenbot.commands.roles.BUMP_NOTIFICATIONS_ROLE_ID
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.util.inc
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class BumpNotificationListener : ListenerAdapter() {

    companion object {
        private const val DISBOARD_BOT_ID = 302050872383242240
        private val BUMP_COOLDOWN = TimeUnit.HOURS.toMillis(2)
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {

        if (event.author.idLong != DISBOARD_BOT_ID) {
            return
        }

        if(!event.message.embeds.first().description?.contains(":thumbsup:")!!) {
            return
        }

        StatsUsers[event.message.mentionedUsers[0].idLong].bumps++

        GlobalScope.launch {
            delay(BUMP_COOLDOWN)
            val bumpNotificationRole =
                    requireNotNull(event.jda.getRoleById(BUMP_NOTIFICATIONS_ROLE_ID)) { "Bump Notifications role not found" }
            event.channel.sendMessage("${bumpNotificationRole.asMention}, the server is ready to be bumped! **!d bump**")
                    .await()
        }
    }
}
