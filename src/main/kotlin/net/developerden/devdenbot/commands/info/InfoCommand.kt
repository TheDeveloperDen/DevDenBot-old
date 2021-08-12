package net.developerden.devdenbot.commands.info

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.commands.management.ManagingCategory
import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.events.EventType
import net.developerden.devdenbot.events.Messages
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.util.VersionProvider
import net.developerden.devdenbot.util.formatNumber
import net.dv8tion.jda.api.EmbedBuilder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
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

        val all = StatsUsers.all()
        val totalXP = formatForInfo(all.sumOf { it.xp })
        val totalMembers = formatForInfo(event.guild.memberCount)
        val dateCreated = formatForInfo(formatDate(event.guild.timeCreated.toLocalDate()))
        val levelUps = formatForInfo(all.sumOf { it.level })
        val formattedVersion = formatForInfo(VersionProvider.version)
        val messageCount = formatForInfo(newSuspendedTransaction {
            Messages.select { Messages.type eq Messages.Type.CREATE }.count()
        })
        reply(EmbedBuilder()
            .setTitle("Developer Den")
            .setDescription("Mildly interesting stats and info")
            .setColor(ddbConfig.colour)
            .addField("Version", formattedVersion, true)
            .addField("Total XP Given", totalXP, true)
            .addField("Total Members", totalMembers, true)
            .addField("Date Created", dateCreated, true)
            .addField("Total Messages Sent", messageCount, true)
            .addField("Level Ups", levelUps, true)
            .build())
    }


}
