package net.developerden.devdenbot.faq

import com.google.inject.Inject
import net.developerden.devdenbot.discord.awaitThenDelete
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.listener.EventListener
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.trait.HasConfig
import net.developerden.devdenbot.util.handleEachIn
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

@Used
class FAQShorthandListener @Inject constructor(override val ddbConfig: DDBConfig) : EventListener, HasConfig {
    companion object {
        private const val FAQ_PREFIX = "?"
        private val FAQ_IDENTIFIER_REGEX = Regex("\\w+") // TODO: also use on faq creation

    }

    private suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!event.message.contentRaw.startsWith(FAQ_PREFIX) || event.author.isBot) {
            return
        }
        val faq = event.message.contentRaw.drop(1)
        if (!FAQ_IDENTIFIER_REGEX.matches(faq)){
            return
        }
        event.channel.sendMessageEmbeds(
            displayFAQ(faq, event.author)
        ).awaitThenDelete()
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>().handleEachIn(scope, this::onGuildMessageReceived)
    }
}
