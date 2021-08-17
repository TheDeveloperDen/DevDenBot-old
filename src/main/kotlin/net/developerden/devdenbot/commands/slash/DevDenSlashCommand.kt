package net.developerden.devdenbot.commands.slash

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.discord.DEV_DEN_SERVER_ID
import net.developerden.devdenbot.extensions.await
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction

abstract class DevDenSlashCommand(name: String, description: String) : DevDenCommand(name, help = description) {

    abstract suspend fun load(action: CommandCreateAction)

    final override suspend fun CommandEvent.execute() {
        reply("This command must be used via a slash command")
    }

    abstract suspend fun SlashCommandEvent.execute()

    suspend fun register(jda: JDA) {
        val guild = jda.getGuildById(DEV_DEN_SERVER_ID) ?: error("cannot find dev den")

        val action = guild.upsertCommand(name, help)
        load(action)
        action.await()

    }
}
