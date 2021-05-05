package me.bristermitten.devdenbot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import kotlinx.coroutines.launch
import me.bristermitten.devdenbot.commands.category.MiscCategory
import me.bristermitten.devdenbot.extensions.commands.tempReply
import me.bristermitten.devdenbot.util.botCommandsChannelId
import me.bristermitten.devdenbot.util.log
import me.bristermitten.devdenbot.util.scope
import net.dv8tion.jda.api.Permission

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
    vararg aliases: String = emptyArray(),
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

    private val logger by log()

    final override fun execute(event: CommandEvent) {
        scope.launch {
            if (event.channel.idLong != botCommandsChannelId && !event.member.hasPermission(Permission.MESSAGE_MANAGE)) {
                event.tempReply("Commands can only be used in<#$botCommandsChannelId>.", 5)
                return@launch
            }
            try {
                event.execute()
            } catch (exception: Exception){
                event.channel.sendMessage(
                    "Could not execute command. Stacktrace: ```${
                        exception.stackTrace.joinToString(
                            "\n",
                            limit = 50
                        )
                    }```"
                )
                logger.error("Could not execute command for event $event.", exception)
            }
        }
    }

}
