package net.developerden.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.arguments.arguments
import net.developerden.devdenbot.commands.slash.DevDenSlashCommand
import net.developerden.devdenbot.data.StatsUserDAO
import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.discord.getPing
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.awaitReply
import net.developerden.devdenbot.extensions.commands.getUser
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
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

    override suspend fun SlashCommandEvent.execute() {
        val target = getOption("target")?.asUser ?: error("Invalid user")
        val field = getOption("field")?.asString ?: error("Invalid field")
        val value = getOption("value")?.asLong ?: error("Invalid value")

        @Suppress("UNCHECKED_CAST")
        val stat = when (field) {
            "xp" -> StatsUserDAO::setXP
            "level" -> StatsUserDAO::setLevel
            "bumps" -> StatsUserDAO::setBumps
            else -> {
                reply("I don't recognise $field").await()
                return
            }
        } as KSuspendFunction2<StatsUserDAO, Number, Unit>

        val statsUser = StatsUsers.get(target.idLong)
        stat(statsUser, value)
        reply("Successfully set value of `${stat.name}` for `${target.name}#${target.discriminator}` to `$value`").await()
    }
}
