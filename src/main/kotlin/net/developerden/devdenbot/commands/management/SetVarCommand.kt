package net.developerden.devdenbot.commands.management

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import net.developerden.devdenbot.commands.slash.DevDenSlashCommand
import net.developerden.devdenbot.data.StatsUserDAO
import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.embedDefaults
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.trait.HasConfig
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.surround
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction
import java.awt.Color
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KSuspendFunction2


/**
 * @author AlexL
 */
@Used
class SetVarCommand @Inject constructor(
    override val ddbConfig: DDBConfig,
) : DevDenSlashCommand(
    name = "set",
    description = "Set the data of a user"
), HasConfig {
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

    @Suppress("UNCHECKED_CAST")
    private fun castSet(t: Any) = t as KSuspendFunction2<StatsUserDAO, Number, Unit>


    private val setter = mapOf(
        "xp" to castSet(StatsUserDAO::setXP),
        "level" to castSet(StatsUserDAO::setLevel),
        "bumps" to castSet(StatsUserDAO::setBumps),
    )


    private val getter = mapOf(
        "xp" to (StatsUserDAO::xp),
        "level" to (StatsUserDAO::level),
        "bumps" to (StatsUserDAO::bumps),
    )


    override suspend fun SlashCommandEvent.execute() {
        if(!member!!.isOwner) {
            reply("You cannot use this command").setEphemeral(true).await()
        }
        val target = getOption("target")?.asUser ?: error("Invalid user")
        val field = getOption("field")?.asString ?: error("Invalid field")
        val value = getOption("value")?.asLong ?: error("Invalid value")

        val setter = setter[field] ?: error("could not find setter for $field")
        val getter = getter[field] ?: error("could not find getter for $field")

        val statsUser = StatsUsers.get(target.idLong)

        val uuid = UUID.randomUUID().toString()
        val hook = replyEmbeds(embedDefaults {
            title = "Confirm"
            addField("Current Value", getter(statsUser).toString().surround("`"), true)
            addField("New Value", value.toString().surround("`"), true)
        }).addActionRow(
            Button.primary("${uuid}confirm", "Set value"),
            Button.danger("${uuid}cancel", "Cancel")
        ).await()


        val clickEvent = jda.listenFlow<ButtonClickEvent>()
            .filter {
                it.componentId.startsWith(uuid)
            }
            .first()

        val realId = clickEvent.componentId.removePrefix(uuid)
        if (realId == "confirm") {
            setter(statsUser, value)

            hook.editOriginalEmbeds(embedDefaults {
                color = Color.GREEN
                title = "Success"
                description =
                    "Successfully set value of `${field}` for `${target.name}#${target.discriminator}` to `$value`"
            }).await()
            hook.editOriginalComponents().await()

            clickEvent.reply("Done").setEphemeral(true).await()
        } else {
            hook.deleteOriginal().await()
            clickEvent.reply("**Cancelled**").setEphemeral(true).await()
        }
    }
}
