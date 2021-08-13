package net.developerden.devdenbot.commands.info

import net.developerden.devdenbot.commands.slash.DevDenSlashCommand
import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.events.Messages
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.embedDefaults
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.trait.HasConfig
import net.developerden.devdenbot.util.VersionProvider
import net.developerden.devdenbot.util.formatNumber
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction
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
    override val ddbConfig: DDBConfig,
) : DevDenSlashCommand(
    name = "info",
    description = "View bot info and some cool stats",
), HasConfig {

    private fun formatDate(dt: LocalDate) = DateTimeFormatter.ofPattern("YYYY MM dd").format(dt)
    override fun load(action: CommandCreateAction) = Unit


    override suspend fun SlashCommandEvent.execute() {
        fun formatForInfo(s: String) = "`$s`"
        fun formatForInfo(s: Number) = "`${formatNumber(s)}`"

        val all = StatsUsers.all()
        val totalXP = formatForInfo(all.sumOf { it.xp })
        val currentMembers = formatForInfo(guild!!.memberCount)
        val totalMembersStored = formatForInfo(all.size)
        val dateCreated = formatForInfo(formatDate(guild!!.timeCreated.toLocalDate()))
        val levelUps = formatForInfo(all.sumOf { it.level })
        val formattedVersion = formatForInfo(VersionProvider.version)
        val messageCount = formatForInfo(newSuspendedTransaction {
            Messages.select { Messages.type eq Messages.Type.CREATE }.count()
        })

        replyEmbeds(embedDefaults {
            title = "Developer Den"
            description = "Mildly interesting stats and info"
            addField("Version", formattedVersion, true)
            addField("Total XP Given", totalXP, true)
            addField("Current Member Count", currentMembers, true)
            addField("Total Members Stored", totalMembersStored, true)
            addField("Date Created", dateCreated, true)
            addField("Total Messages Sent", messageCount, true)
            addField("Level Ups", levelUps, true)
        }).await()
    }


}
