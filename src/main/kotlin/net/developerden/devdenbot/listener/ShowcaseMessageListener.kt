package net.developerden.devdenbot.listener

import net.developerden.devdenbot.discord.SHOWCASE_CHANNEL_ID
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.util.handleEachIn
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent

@Used
class ShowcaseMessageListener : EventListener {
    companion object {
        private const val PLUS_ONE = "\uD83D\uDC4D"
        private const val MINUS_ONE = "\uD83D\uDC4E"
    }

    private suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.channel.idLong != SHOWCASE_CHANNEL_ID) {
            return
        }
        event.message.addReaction(PLUS_ONE).await()
        event.message.addReaction(MINUS_ONE).await()
    }

    private suspend fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (event.channel.idLong != SHOWCASE_CHANNEL_ID) {
            return
        }
        val message = event.retrieveMessage().await()
        if (event.userIdLong == message.author.idLong) {
            event.reaction.removeReaction(event.user).await()
            return
        }
        if (
            message.reactions
                .filter {
                    it.retrieveUsers().await()
                        .any { user ->
                            user.idLong == event.userIdLong
                        }
                }.filter {
                    it.reactionEmote.emoji == PLUS_ONE || it.reactionEmote.emoji == MINUS_ONE
                }.size >= 2
        ) {
            event.reaction.removeReaction(event.user).await()
        }
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>().handleEachIn(scope, this::onGuildMessageReceived)
        jda.listenFlow<GuildMessageReactionAddEvent>().handleEachIn(scope, this::onGuildMessageReactionAdd)
    }
}
