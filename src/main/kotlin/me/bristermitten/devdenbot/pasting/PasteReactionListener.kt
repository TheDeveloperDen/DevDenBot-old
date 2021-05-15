package me.bristermitten.devdenbot.pasting

import me.bristermitten.devdenbot.discord.HELPFUL_ROLE_ID
import me.bristermitten.devdenbot.discord.PASTE_EMOJI_ID
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.listener.EventListener
import me.bristermitten.devdenbot.util.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import javax.inject.Inject

class PasteReactionListener @Inject constructor(val jda: JDA) : EventListener {

    companion object {
        private val log by log()
    }

    //Getting started category
    private val category = jda.getCategoryById(821743100657270874L)

    private suspend fun onReactionAdd(event: MessageReactionAddEvent) {

        if (!event.reactionEmote.isEmote) {
            // only consider custom emojis
            return
        }

        if (event.reactionEmote.emote.idLong != PASTE_EMOJI_ID) {
            return
        }

        category?.textChannels?.contains(event.channel)?.also {
            if (it) return@onReactionAdd
        }

        val reactionMember =
            event.user
                ?.let { event.guild.getMember(it) }
                ?: event.guild.retrieveMemberById(event.userId).await()

        if (reactionMember.user.isBot) {
            log.warn { "Bot ${reactionMember.user.name} tried to use the paste reaction command. Only humans may perform this action." }
            return
        }

        if (!hasRoleOrIsModerator(reactionMember, HELPFUL_ROLE_ID)) { // this is vital
            log.debug { "User ${reactionMember.user.name} has insufficient permissions to perform paste reactions." }
            return
        }

        val message = event.retrieveMessage().await()
        val mention = message.author.asMention
        val pasteUrl = HasteClient.postCode(message.contentStripped)

        message.delete().queue()

        val pasteMessage = "$mention, your code is available at $pasteUrl"
        event.channel.sendMessage(pasteMessage).queue()
    }

    override fun register(jda: JDA) {
        jda.listenFlow<MessageReactionAddEvent>().launchEachIn(scope, this::onReactionAdd)
    }
}
