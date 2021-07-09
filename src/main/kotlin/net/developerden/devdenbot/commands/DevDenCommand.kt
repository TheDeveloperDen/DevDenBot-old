package net.developerden.devdenbot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import io.sentry.Sentry
import kotlinx.coroutines.launch
import net.developerden.devdenbot.commands.category.MiscCategory
import net.developerden.devdenbot.extensions.commands.tempReply
import net.developerden.devdenbot.discord.BOT_COMMANDS_CHANNEL_ID
import net.developerden.devdenbot.util.log
import net.developerden.devdenbot.util.scope
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
            if (commandChannelOnly && event.channel.idLong != BOT_COMMANDS_CHANNEL_ID
                    && !event.member.hasPermission(Permission.MESSAGE_MANAGE)) {
                log.trace { "Member ${event.member.user.name} has insufficient permissions to execute commands in channel ${event.channel.name}."}
                event.tempReply("Commands can only be used in<#$BOT_COMMANDS_CHANNEL_ID>.")
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
                Sentry.captureException(exception)
                exception.printStackTrace()
                event.channel.sendMessage("**An error occurred when executing this command. This has been logged.**")
                    .queue()
            }
        }
    }

}
