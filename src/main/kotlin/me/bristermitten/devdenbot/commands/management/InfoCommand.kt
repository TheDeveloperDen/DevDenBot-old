package me.bristermitten.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.stats.GlobalStats
import net.dv8tion.jda.api.EmbedBuilder
import java.math.BigInteger
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Wood (BristerMitten)
 */
class InfoCommand @Inject constructor(
    private val ddbConfig: DDBConfig,
) : DevDenCommand(
    name = "info",
    help = "View bot info and some cool stats",
    category = ManagingCategory,
    aliases = arrayOf("flex", "stats")
) {

    private val version by lazy {
        javaClass.classLoader.getResourceAsStream("version")!!.reader().readText()
    }

    private fun formatNumber(a: Any) = "`${NumberFormat.getNumberInstance(Locale.US).format(a)}`"
    private fun formatDate(dt: LocalDate) = "`${DateTimeFormatter.ofPattern("YYYY MM dd").format(dt)}`"

    override suspend fun CommandEvent.execute() {
        val totalXP = formatNumber(StatsUsers.all.map { it.xp }.reduce(BigInteger::add))
        val totalMembers = formatNumber(event.guild.memberCount)
        val dateCreated = formatDate(event.guild.timeCreated.toLocalDate())
        val totalMessages = formatNumber(GlobalStats.totalMessagesSent)
        val totalXPGiven = formatNumber(GlobalStats.xpGiven)
        val levelUps = formatNumber(GlobalStats.levelUps)

        reply(EmbedBuilder()
            .setTitle("Developer Den")
            .setDescription("Mildly interesting stats and info")
            .setColor(ddbConfig.colour)
            .addField("Total XP Given", totalXP, true)
            .addField("Total Members", totalMembers, true)
            .addField("Date Created", dateCreated, true)
            .addField("Total Messages Sent", totalMessages, true)
            .addField("Total XP Given", totalXPGiven, true)
            .addField("Level Ups", levelUps, true)
            .build())
    }


}
