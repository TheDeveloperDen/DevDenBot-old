package net.developerden.devdenbot.log

import ch.qos.logback.classic.Level
import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.commands.arguments.arguments
import net.developerden.devdenbot.commands.management.ManagingCategory
import net.developerden.devdenbot.commands.senderMustHaveRole
import net.developerden.devdenbot.discord.BOT_CONTRIBUTOR_ROLE_ID
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.inject.Used

@Used
class LogLevelCommand : DevDenCommand(
    name = "loglevel",
    help = "Set the bot's logging level (administrator only)",
    category = ManagingCategory,
    arguments = "<level>"
) {

    override suspend fun CommandEvent.execute() {
        senderMustHaveRole(BOT_CONTRIBUTOR_ROLE_ID)

        val levelName = arguments().args.first().content
        val level = when (levelName.lowercase()) { // Fake enum :(
            "off" -> Level.OFF
            "error", "severe" -> Level.ERROR
            "warn", "warning" -> Level.WARN
            "info" -> Level.INFO
            "debug" -> Level.DEBUG
            "trace", "fine" -> Level.TRACE
            "all" -> Level.ALL
            else -> {
                message.reply("Unknown Log Level $levelName").await()
                return
            }
        }

        setLoggingLevel(level)

        message.reply("**Logging Level set to `$level`.**").await()
    }

}
