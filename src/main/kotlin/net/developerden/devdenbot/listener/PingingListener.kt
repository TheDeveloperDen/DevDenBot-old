package net.developerden.devdenbot.listener

import com.google.inject.Inject
import net.developerden.devdenbot.discord.awaitThenDelete
import net.developerden.devdenbot.discord.getPing
import net.developerden.devdenbot.discord.isStaff
import net.developerden.devdenbot.discord.shouldNotBePinged
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.embedDefaults
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.trait.HasConfig
import net.developerden.devdenbot.util.addAndToCommaSeparated
import net.developerden.devdenbot.util.handleEachIn
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Used
class PingingListener @Inject constructor(override val ddbConfig: DDBConfig) : EventListener, HasConfig {

    @OptIn(ExperimentalTime::class)
    suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot || event.message.referencedMessage != null) {
            return
        }

        if (event.member?.isStaff() == true) {
            return
        }

        val thoseWhoShouldNotHaveBeenPinged = event.message.mentionedMembers.filter {
            it.shouldNotBePinged() && it.idLong != event.author.idLong
        }
        if (thoseWhoShouldNotHaveBeenPinged.isEmpty()) {
            return
        }
        val formattedNoPinged = thoseWhoShouldNotHaveBeenPinged.joinToString(",") {
            it.getPing()
        }.addAndToCommaSeparated()

        val authorPing = event.member?.getPing() ?: return
        event.channel.sendMessageEmbeds(embedDefaults {
            title = "Anti Ping"
            description = """
                ${authorPing}, the following users have asked to not be pinged: $formattedNoPinged
                Please respect their decisions and try not to ping them!
            """.trimIndent()
        }).awaitThenDelete(Duration.seconds(10))
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>().handleEachIn(scope, this::onGuildMessageReceived)
    }
}
