package me.bristermitten.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.AtomicBigInteger
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.VersionProvider
import me.bristermitten.devdenbot.util.formatNumber
import net.dv8tion.jda.api.EmbedBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * @author Alexander Wood (BristerMitten)
 */
@Used
class InfoCommand @Inject constructor(
    private val ddbConfig: DDBConfig,
) : DevDenCommand(
    name = "info",
    help = "View bot info and some cool stats",
    category = ManagingCategory,
    aliases = arrayOf("flex", "stats")
) {

    private fun formatDate(dt: LocalDate) = DateTimeFormatter.ofPattern("YYYY MM dd").format(dt)

    override suspend fun CommandEvent.execute() {
        fun formatForInfo(s: String) = "`$s`"
        fun formatForInfo(s: Number) = "`${formatNumber(s)}`"

        val totalXP = formatForInfo(StatsUsers.all().sumOf { it.xp })
        val totalMembers = formatForInfo(event.guild.memberCount)
        val dateCreated = formatForInfo(formatDate(event.guild.timeCreated.toLocalDate()))
        val levelUps = formatForInfo(StatsUsers.all().sumOf { it.level })
        val formattedVersion = formatForInfo(VersionProvider.version)

        reply(EmbedBuilder()
            .setTitle("Developer Den")
            .setDescription("Mildly interesting stats and info")
            .setColor(ddbConfig.colour)
            .addField("Version", formattedVersion, true)
            .addField("Total XP Given", totalXP, true)
            .addField("Total Members", totalMembers, true)
            .addField("Date Created", dateCreated, true)
            .addField("Total Messages Sent", "Currently disabled :frowning2:", true)
            .addField("Level Ups", levelUps, true)
            .build())
    }


}
