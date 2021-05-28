package me.bristermitten.devdenbot.listener

import me.bristermitten.devdenbot.discord.SHOWCASE_CHANNEL_ID
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.util.handleEachIn
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

@Used
class ShowcaseMessageListener : EventListener {
    companion object {
        private const val PLUS_ONE = "thumbsup"
        private const val MINUS_ONE = "thumbsdown"
    }

    suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.channel.idLong != SHOWCASE_CHANNEL_ID) {
            return
        }
        event.message.addReaction(PLUS_ONE).await()
        event.message.addReaction(MINUS_ONE).await()
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>().handleEachIn(scope, this::onGuildMessageReceived)
    }
}
