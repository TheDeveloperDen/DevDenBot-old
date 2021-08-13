package net.developerden.devdenbot.commands.slash

import net.developerden.devdenbot.commands.DevDenCommand
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction

abstract class DevDenSlashCommand(name: String, description: String) : DevDenCommand(name, help = description) {

    abstract fun load(action: CommandCreateAction)
}
