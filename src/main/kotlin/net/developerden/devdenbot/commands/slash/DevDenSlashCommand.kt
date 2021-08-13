package net.developerden.devdenbot.commands.slash

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction

abstract class DevDenSlashCommand(name: String, description: String) : DevDenCommand(name, help = description) {

    abstract fun load(action: CommandCreateAction)

    final override suspend fun CommandEvent.execute() {
        reply("This command must be used via a slash command")
    }

    abstract suspend fun SlashCommandEvent.execute()
}
