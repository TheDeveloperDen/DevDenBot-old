package net.developerden.devdenbot.xp.commands

import net.developerden.devdenbot.commands.slash.DevDenSlashCommand
import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.embedDefaults
import net.developerden.devdenbot.graphics.createTextImage
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.trait.HasConfig
import net.developerden.devdenbot.util.formatNumber
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction
import java.awt.Color
import javax.inject.Inject


/**
 * @author AlexL
 */
@Used
class XPCommand @Inject constructor(
    override val ddbConfig: DDBConfig,
) : DevDenSlashCommand(
    name = "xp",
    description = "View the xp of a user",
), HasConfig {

    companion object {
        private val defaultColor = Color.decode("0xFFA500")
    }

    override fun load(action: CommandCreateAction) {
        action.addOption(OptionType.USER, "target", "User to get the xp of", false)
    }

    override suspend fun SlashCommandEvent.execute() {
        val target = getOption("target")?.asUser ?: user
        val targetStatsUser = StatsUsers.get(target.idLong)
        val text = formatNumber(targetStatsUser.xp) + " XP"

        val userColor = guild!!.getMember(target)?.color ?: defaultColor

        val photo = createTextImage(
            width = 1000,
            height = 500,
            text = text,
            fontColor = userColor,
            backgroundColor = Color.darkGray)

        val message = embedDefaults {
            title = "XP of ${target.name}#${target.discriminator}"
            setFooter("Developer Den", target.effectiveAvatarUrl)

            setImage("attachment://xp.png")
        }

        replyEmbeds(message)
            .addFile(photo, "xp.png")
            .await()
    }
}
