package me.bristermitten.devdenbot.commands.management

import com.google.inject.Provider
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.jagrosh.jdautilities.menu.ButtonMenu
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.commands.category.Categories
import me.bristermitten.devdenbot.commands.category.CommandCategory
import me.bristermitten.devdenbot.extensions.arguments
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.extensions.commands.awaitReply
import me.bristermitten.devdenbot.serialization.DDBConfig
import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import javax.inject.Inject

/**
 * @author Alexander Wood (BristerMitten)
 */
class HelpCommand @Inject constructor(
    private val commandClient: Provider<CommandClient>,
    private val DDBConfig: DDBConfig,
    private val eventWaiter: EventWaiter
) : DevDenCommand(
        name = "help",
        help = "View help",
        category = ManagingCategory
    ) {

    private val choices by lazy {
        Categories.associateBy({ it.emoji.name }, { it })
    }

    override suspend fun CommandEvent.execute() {
        val args = arguments()
        if (args.isEmpty()) {
            val complete = event.channel.sendMessage("Dev Den Help").await()
            sendMainMessage(complete)
            return
        }


        val category = Categories.byName(args.first().content)
        if (category == null) {
            awaitReply("I couldn't find a category named \"$category\"")
            return
        }

        val complete = event.channel.sendMessage("Dev Den Help").await()
        sendHelpMenu(complete, category)
    }


    private fun sendMainMessage(message: Message) {
        ButtonMenu.Builder()
            .setColor(Color(DDBConfig.colour))
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
                sendHelpMenu(message, choices[it.name] ?: error(""))
            }
            .setFinalAction {
                it.clearReactions().complete()
            }
            .build()
            .display(message)
    }

    private fun sendHelpMenu(message: Message, category: CommandCategory) {
        val commands = commandClient.get().commands
            .filter { it.category == category }

        val description = """
            |${category.emoji.asMention} ${category.description}
            |${commands.joinToString("\n") { "`${DDBConfig.prefix}${it.name}` - ${it.help}" }}
            |
            |Type `!help ${category.shortName}` for more detail.
        """.trimMargin()


        ButtonMenu.Builder()
            .setColor(Color(DDBConfig.colour))
            .setDescription(description)
            .setEventWaiter(eventWaiter)
            .addChoices("⬅️")
            .setAction {
                sendMainMessage(message)
            }
            .setFinalAction {
                it.clearReactions().queue()
            }
            .build()
            .display(message)
    }
}
