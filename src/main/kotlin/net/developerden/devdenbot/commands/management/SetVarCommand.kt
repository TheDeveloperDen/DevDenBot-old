package net.developerden.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.commands.arguments.arguments
import net.developerden.devdenbot.commands.slash.DevDenSlashCommand
import net.developerden.devdenbot.data.StatsUserDAO
import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.discord.getPing
import net.developerden.devdenbot.extensions.commands.awaitReply
import net.developerden.devdenbot.extensions.commands.getUser
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction
import java.math.BigInteger
import javax.inject.Inject
import kotlin.reflect.KSuspendFunction2
import kotlin.reflect.KType
import kotlin.reflect.full.createType


/**
 * @author AlexL
 */
@Used
class SetVarCommand @Inject constructor(
    val config: DDBConfig,
) : DevDenSlashCommand(
    name = "set",
    description = "Set the data of a user"
) {
    override fun load(action: CommandCreateAction) {
        action
            .addOption(OptionType.USER, "target", "the user to edit", true)
            .addOptions(OptionData(OptionType.STRING, "field", "what to edit", true).addChoices(
                Command.Choice("xp", "xp"),
                Command.Choice("level", "level"),
                Command.Choice("bumps", "bumps")
            ))
            .addOption(OptionType.INTEGER, "value", "The value to set", true)

    }

    override suspend fun CommandEvent.execute() {
        val arguments = arguments()
        val args = arguments.args

        arguments.validateArgLength(3) {
            awaitReply("Not enough arguments. Expected format: `${config.prefix}set ${this@SetVarCommand.arguments}`")
            return
        }

        val targetUser = getUser() ?: event.message.author

        val field = args[1].content

        @Suppress("UNCHECKED_CAST")
        val stat = when (field) {
            "xp" -> StatsUserDAO::setXP
            "lvl", "level" -> StatsUserDAO::setLevel
            "bump", "bumps" -> StatsUserDAO::setBumps
            else -> {
                awaitReply("I don't recognise $field")
                return
            }
        } as KSuspendFunction2<StatsUserDAO, Any, Unit>

        val valueInput = args[2]
        val type = stat.parameters[1].type
        val value = try {
            valueInput.content.parseTo(type)
        } catch (e: Exception) {
            awaitReply("**Invalid value**. Expected type `${type}` but could not parse `${valueInput.content}` to this type.")
            return
        }

        val statsUser = StatsUsers.get(targetUser.idLong)
        stat(statsUser, value)
        val targetMember = guild.getMember(targetUser) ?: member


        awaitReply("Successfully set value of `${stat.name}` for ${targetMember.getPing()} to `$value`")
    }

    private fun String.parseTo(type: KType) = when (type) {
        String::class.createType() -> this
        Int::class.createType() -> toInt()
        Long::class.createType() -> toLong()
        BigInteger::class.createType() -> toBigInteger()
        else -> throw IllegalArgumentException("Cannot parse $type")
    }
}
