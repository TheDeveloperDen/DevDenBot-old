package me.bristermitten.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.events.EventType
import me.bristermitten.devdenbot.events.Events
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.VersionProvider
import me.bristermitten.devdenbot.util.formatNumber
import me.bristermitten.devdenbot.util.log
import me.bristermitten.devdenbot.xp.processLevelUp
import me.bristermitten.devdenbot.xp.xpForLevel
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
class FixXPCommand @Inject constructor(
    private val ddbConfig: DDBConfig,
) : DevDenCommand(
    name = "fixxp",
    help = "Fix broken XP",
    category = ManagingCategory,
    ownerCommand = true
) {

    override suspend fun CommandEvent.execute() {
        newSuspendedTransaction {
            val all = StatsUser.all()
            for(user in all) {
                val member = guild.getMemberById(user.id.value) ?: continue // user probably left the guild
                while(xpForLevel(user.level) <= user.xp){
                    processLevelUp(member, ++user.level)
                }
            }
        }

    }


}
