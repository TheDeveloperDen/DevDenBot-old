package me.bristermitten.devdenbot.listener

import club.minnced.jda.reactor.onMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.discord.BUMP_NOTIFICATIONS_ROLE_ID
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.util.inc
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.concurrent.TimeUnit

@Used
class BumpNotificationListener : EventListener {

    companion object {
        private const val DISBOARD_BOT_ID = 302050872383242240
        private val BUMP_COOLDOWN = TimeUnit.HOURS.toMillis(2)
    }

    private suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.message.contentRaw != "!d bump") {
            return
        }

        val nextDisboardMessages = event.channel.onMessage()
            .asFlow()
            .filter { it.author.idLong == DISBOARD_BOT_ID }
            .map { it.message.embeds }
            .filter { it.isNotEmpty() }
            .firstOrNull() ?: return

        val nextDisboardMessage = nextDisboardMessages.firstOrNull() ?: return

        if (nextDisboardMessage.description?.contains(":thumbsup:") != true) {
            return
        }

        StatsUsers[event.author.idLong].bumps++

        delay(BUMP_COOLDOWN)

        val bumpNotificationRole = requireNotNull(event.jda.getRoleById(BUMP_NOTIFICATIONS_ROLE_ID)) {
            "Bump Notifications role not found"
        }

        event.channel.sendMessage(
            "${bumpNotificationRole.asMention}, the server is ready to be bumped! **!d bump**"
        ).await()
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>().onEach(this::onGuildMessageReceived).launchIn(scope)
    }
}
