package net.developerden.devdenbot.xp.commands

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.commands.slash.DevDenSlashCommand
import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.embedDefaults
import net.developerden.devdenbot.extensions.commands.getUser
import net.developerden.devdenbot.extensions.commands.prepareReply
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.trait.HasConfig
import net.developerden.devdenbot.util.formatNumber
import net.developerden.devdenbot.xp.tierOf
import net.developerden.devdenbot.xp.xpForLevel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction
import javax.inject.Inject
import kotlin.coroutines.resume


/**
 * @author AlexL
 */
@Used
class ProfileCommand @Inject constructor(
    override val ddbConfig: DDBConfig
) : DevDenSlashCommand(
    name = "profile",
    description = "View your or someone else's profile"
), HasConfig {

    override fun load(action: CommandCreateAction) {
        action.addOption(OptionType.USER, "target", "User to get the profile of", false)
    }

    override suspend fun SlashCommandEvent.execute() {
        val targetUser = getOption("target")?.asUser ?: user

        val statsUser = StatsUsers.get(targetUser.idLong)

        val action = embedDefaults {
            title = "Your Statistics"
            field("XP", formatNumber(statsUser.xp), true)
            field("Level", statsUser.level.toString(), true)
            field("Tier", tierOf(statsUser.level).toString(), true)
            field("Disboard Bumps", statsUser.bumps.toString(), true)
            field("XP to Level", formatNumber(xpForLevel(statsUser.level + 1)), true)
            setFooter("Statistics for ${targetUser.name}#${targetUser.discriminator}")
        }
        replyEmbeds(action).await()
    }
}
