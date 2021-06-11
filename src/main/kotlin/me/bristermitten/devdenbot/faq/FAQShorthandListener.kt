package me.bristermitten.devdenbot.faq

import com.google.inject.Inject
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.listener.EventListener
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.trait.HasConfig
import me.bristermitten.devdenbot.util.handleEachIn
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
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
        event.channel.sendMessage(
            displayFAQ(faq, event.author)
        ).await()
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>().handleEachIn(scope, this::onGuildMessageReceived)
    }
}
