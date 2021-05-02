package me.bristermitten.devdenbot.listener

import club.minnced.jda.reactor.onMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class BumpNotificationListener : ListenerAdapter() {
    companion object {
        private const val DISBOARD_BOT_ID = 302050872383242240
        private val BUMP_COOLDOWN = TimeUnit.HOURS.toMillis(2)
        const val BUMP_NOTIFICATIONS_ROLE_ID = 838500233268691005
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.message.contentRaw != "!d bump") {
            return
        }
        GlobalScope.launch {
            event.channel.onMessage()
                .asFlow()
                .filter { it.author.idLong == DISBOARD_BOT_ID }
                .map { it.message.embeds }
                .filter { it.isNotEmpty() }
                .filter { it.first().description?.contains(":thumbsup:") ?: false } //hacky but works
                .collect {
                    delay(BUMP_COOLDOWN)
                    val bumpNotificationRole =
                        requireNotNull(event.jda.getRoleById(BUMP_NOTIFICATIONS_ROLE_ID)) { "Bump Notifications role not found" }
                    event.channel.sendMessage("${bumpNotificationRole.asMention}, the server is ready to be bumped! **!d bump**")
                }

        }
    }
}
