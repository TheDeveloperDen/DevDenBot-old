package net.developerden.devdenbot.commands.info

import com.google.inject.Provider
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.jagrosh.jdautilities.menu.ButtonMenu
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.commands.arguments.arguments
import net.developerden.devdenbot.commands.category.Categories
import net.developerden.devdenbot.commands.category.CommandCategory
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.awaitReply
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.util.isAdmin
import net.developerden.devdenbot.util.log
import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import javax.inject.Inject

/**
 * @author Alexander Wood (BristerMitten)
 */
@Used
class HelpCommand @Inject constructor(
    private val commandClient: Provider<CommandClient>,
    private val ddbConfig: DDBConfig,
    private val eventWaiter: EventWaiter,
) : DevDenCommand(
    name = "help",
    help = "View help",
    category = InfoCategory
) {

    companion object {
        private val log by log()
    }

    private val choices by lazy {
        Categories.associateBy({ it.emoji.name }, { it })
    }

    override suspend fun CommandEvent.execute() {
        val args = arguments().args
        val adminMode = arguments().flags.any { it.equals("owner") || it.equals("admin") }

        if (adminMode && !isAdmin(member)) {
            log.trace { "Blocked ${member.user.name} from calling the help command in admin mode." }
            reply("You need to have administrator permissions to execute this command in admin mode.")
            return
        }

        if (args.isEmpty()) {
            val complete = event.channel.sendMessage("Dev Den Help").await()
            sendMainMessage(complete, adminMode)
            return
        }


        val category = Categories.byName(args.first().content)
        if (category == null) {
            awaitReply("I couldn't find a category named \"$category\"")
            return
        }

        val complete = event.channel.sendMessage("Dev Den Help").await()
        sendHelpMenu(complete, category, adminMode)
    }


    private fun sendMainMessage(message: Message, adminMode: Boolean) {
        ButtonMenu.Builder()
            .setColor(Color(ddbConfig.colour))
            .setDescription(
                Categories.joinToString("\n\n") {
                    "${it.emoji.asMention} ${it.name}"
                }
            )
            .setText(
                """
                |Commands:
                |
                |**Categories:**
                """.trimMargin()
            )
            .setEventWaiter(eventWaiter)
            .addChoices(*choices.keys.toTypedArray())
            .setAction {
                sendHelpMenu(message, choices[it.name] ?: error(""), adminMode)
            }
            .setFinalAction {
                it.clearReactions().complete()
            }
            .build()
            .display(message)
    }

    private fun sendHelpMenu(message: Message, category: CommandCategory, adminMode: Boolean) {
        val commands = commandClient.get().commands
            .filter { it.category == category }
        val description = """
            |${category.emoji.asMention} ${category.description}
            |${
            commands.filter { adminMode || !it.isOwnerCommand }
                .joinToString("\n") { "`${ddbConfig.prefix}${it.name}` - ${it.help}" }
        }
            |
            |Type `${ddbConfig.prefix}help ${category.shortName}` for more detail.
            ${footer(adminMode)}
        """.trimMargin()


        ButtonMenu.Builder()
            .setColor(Color(ddbConfig.colour))
            .setDescription(description)
            .setEventWaiter(eventWaiter)
            .addChoices("⬅️")
            .setAction {
                sendMainMessage(message, adminMode)
            }
            .setFinalAction {
                it.clearReactions().queue()
            }
            .build()
            .display(message)
    }

    private fun footer(adminMode: Boolean) = if (adminMode)
        """|
           | *This message was executed in admin mode and may contain commands that need admin permissions.*
           """
            .trimIndent()
    else ""
}
