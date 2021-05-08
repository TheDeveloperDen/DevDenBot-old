package me.bristermitten.devdenbot.log

import ch.qos.logback.classic.Level
import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.commands.management.ManagingCategory
import me.bristermitten.devdenbot.commands.senderMustHaveRole
import me.bristermitten.devdenbot.discord.BOT_CONTRIBUTOR_ROLE_ID
import me.bristermitten.devdenbot.extensions.arguments
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used

@Used
class LogLevelCommand : DevDenCommand(
    name = "loglevel",
    help = "Set the bot's logging level (administrator only)",
    category = ManagingCategory,
    arguments = "<level>"
) {

    override suspend fun CommandEvent.execute() {
        senderMustHaveRole(BOT_CONTRIBUTOR_ROLE_ID)

        val levelName = arguments().first().content
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
