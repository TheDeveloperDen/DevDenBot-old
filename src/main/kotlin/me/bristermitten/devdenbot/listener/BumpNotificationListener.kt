package me.bristermitten.devdenbot.listener

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        if (event.author.idLong != DISBOARD_BOT_ID) {
            return
        }

        if (!event.message.embeds.first().description?.contains("Check it on DISBOARD:")!!) {
            return
        }

        StatsUsers[event.message.mentionedUsers[0].idLong].bumps++

        delay(BUMP_COOLDOWN)
        val bumpNotificationRole =
            requireNotNull(event.jda.getRoleById(BUMP_NOTIFICATIONS_ROLE_ID)) { "Bump Notifications role not found" }
        event.channel.sendMessage("${bumpNotificationRole.asMention}, the server is ready to be bumped! **!d bump**")
            .await()
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>().onEach(this::onGuildMessageReceived).launchIn(scope)
    }
}
