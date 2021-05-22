package me.bristermitten.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.xp.processLevelUp
import me.bristermitten.devdenbot.xp.xpForLevel
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import javax.inject.Inject

/**
 * @author Alexander Wood (BristerMitten)
 */
@Used
class FixXPCommand @Inject constructor(
) : DevDenCommand(
    name = "fixxp",
    help = "Fix broken XP",
    category = ManagingCategory,
    ownerCommand = true
) {

    override suspend fun CommandEvent.execute() {
        newSuspendedTransaction {
            val all = StatsUser.all()
            all.forEach { user ->
                println(user.id)
                val member = try {
                    guild.retrieveMemberById(user.id.value).await()
                } catch (e: ErrorResponseException) {
                    println("${user.id.value} was not in guild")
                    return@forEach // user probably left the guild
                }
                while (xpForLevel(user.level + 1) <= user.xp) {
                    processLevelUp(member, ++user.level)
                }
            }
            reply("All level ups given i think")
        }
    }

}


