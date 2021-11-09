package net.developerden.devdenbot.pasting

import net.developerden.devdenbot.discord.PASTE_EMOJI_ID
import net.developerden.devdenbot.discord.STAFF_ROLE_ID
import net.developerden.devdenbot.discord.fetchMember
import net.developerden.devdenbot.discord.getPing
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.listener.EventListener
import net.developerden.devdenbot.util.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent

@Used
class PasteReactionListener : EventListener {

    companion object {
        private val log by log()
    }

    private suspend fun onReactionAdd(event: MessageReactionAddEvent) {

        if (!event.reactionEmote.isEmote) {
            // only consider custom emojis
            return
        }

        if (event.reactionEmote.emote.idLong != PASTE_EMOJI_ID) {
            return
        }

        val reactionMember =
            event.user
                ?.let { event.guild.getMember(it) }
                ?: event.guild.retrieveMemberById(event.userId).await()

        if (reactionMember.user.isBot) {
            log.warn { "Bot ${reactionMember.user.name} tried to use the paste reaction command. Only humans may perform this action." }
            return
        }

        if (!reactionMember.hasRoleOrAbove(STAFF_ROLE_ID)) {
            log.debug { "User ${reactionMember.user.name} has insufficient permissions to perform paste reactions." }
            return
        }

        val message = event.retrieveMessage().await()
        val pasteUrl = HasteClient.postCode(message.contentStripped)

        message.delete().queue()

        val mention = message.fetchMember().getPing()
        val pasteMessage = "$mention, your code is available at $pasteUrl"
        event.channel.sendMessage(pasteMessage).queue()
    }

    override fun register(jda: JDA) {
        jda.listenFlow<MessageReactionAddEvent>().handleEachIn(scope, this::onReactionAdd)
    }
}
