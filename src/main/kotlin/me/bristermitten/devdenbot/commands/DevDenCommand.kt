package me.bristermitten.devdenbot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.bristermitten.devdenbot.commands.category.MiscCategory

/**
 * @author Alexander Wood (BristerMitten)
 */
abstract class DevDenCommand(
    name: String,
    help: String = "No Help Available",
    category: Category? = MiscCategory,
    arguments: String? = null,
    ownerCommand: Boolean = false,
    cooldown: Int = 0,
    vararg aliases: String = emptyArray()
) : Command() {

    init {
        super.name = name
        super.help = help
        super.category = category
        super.arguments = arguments
        super.ownerCommand = ownerCommand
        super.cooldown = cooldown
        super.aliases = aliases
    }

    abstract suspend fun CommandEvent.execute()

    final override fun execute(event: CommandEvent) {
        GlobalScope.launch {
            event.execute()
        }
    }

}
