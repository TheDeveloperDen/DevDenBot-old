package net.developerden.devdenbot.commands.slash

import com.google.inject.Inject
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.listener.EventListener
import net.developerden.devdenbot.util.handleEachIn
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

@Used
class SlashCommandHandler @Inject constructor(val commands: Set<DevDenSlashCommand>) : EventListener {
    private val asMap = commands.associateBy { it.name }

    private suspend fun handle(event: SlashCommandEvent) {
        val command = asMap[event.name] ?: return
        with(command) {
            event.execute()
        }
    }

    override fun register(jda: JDA) {
        jda.listenFlow<SlashCommandEvent>().handleEachIn(scope, this::handle)
    }
}
