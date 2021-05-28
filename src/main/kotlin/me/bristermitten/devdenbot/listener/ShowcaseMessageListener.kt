package me.bristermitten.devdenbot.listener

import me.bristermitten.devdenbot.discord.SHOWCASE_CHANNEL_ID
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.util.handleEachIn
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent

@Used
class ShowcaseMessageListener : EventListener {
    companion object {
        private const val PLUS_ONE = "U+1F44D"
        private const val MINUS_ONE = "U+1F44E"
    }

    suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.channel.idLong != SHOWCASE_CHANNEL_ID) {
            return
        }
        event.message.addReaction(PLUS_ONE).await()
        event.message.addReaction(MINUS_ONE).await()
    }

    suspend fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (event.channel.idLong != SHOWCASE_CHANNEL_ID) {
            return
        }
        val message = event.retrieveMessage().await()
        if (event.userIdLong == message.author.idLong) {
            event.reaction.removeReaction(event.user).await()
            return
        }
        if ( // looks ugly, idk how to improve it lol
            message.reactions
                .filter {
                    it.retrieveUsers().await()
                        .any { user ->
                            user.idLong == event.userIdLong
                        }
                }.filter {
                    it.reactionEmote.asCodepoints == PLUS_ONE || it.reactionEmote.asCodepoints == MINUS_ONE
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
