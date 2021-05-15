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
import java.io.PrintWriter
import java.io.StringWriter

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
    commandChannelOnly: Boolean = true,
) : Command() {

    private val commandChannelOnly: Boolean

    init {
        super.name = name
        super.help = help
        super.category = category
        super.arguments = arguments
        super.ownerCommand = ownerCommand
        super.cooldown = cooldown
        super.aliases = aliases
        this.commandChannelOnly = commandChannelOnly
    }

    abstract suspend fun CommandEvent.execute()

    private val log by log()

    final override fun execute(event: CommandEvent) {
        log.debug { "Executing command $name for ${event.member} in ${event.channel.name}." }
        scope.launch {
            if (commandChannelOnly && event.channel.idLong != botCommandsChannelId
                && !event.member.hasPermission(Permission.MESSAGE_MANAGE)
            ) {
                log.trace { "Member ${event.member.user.name} has insufficient permissions to execute commands in channel ${event.channel.name}." }
                event.tempReply("Commands can only be used in<#$botCommandsChannelId>.", 5)
                return@launch
            }
            try {
                event.execute()
            } catch (preconditionFailed: PreconditionFailedException) {
                log.debug(preconditionFailed) { "Preconditions were not met, command $name was not executed." }
                preconditionFailed.reason?.let {
                    event.reply(it)
                }
            } catch (exception: Exception) {
                val stringWriter = StringWriter()
                exception.printStackTrace(PrintWriter(stringWriter))

                log.error(exception) { "Could not execute command for event $event." }
                event.channel.sendMessage(
                    "Could not execute command. Stacktrace: ```$stringWriter```"
                ).queue()
            }
        }
    }

}
