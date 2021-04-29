package me.bristermitten.devdenbot.commands.xp

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.WHITESPACE_REGEX
import me.bristermitten.devdenbot.extensions.commands.reply
import net.dv8tion.jda.api.JDA
import java.math.BigInteger
import javax.inject.Inject
import kotlin.reflect.KProperty1


/**
 * @author AlexL
 */
class LeaderboardCommand @Inject constructor(
    val jda: JDA,
) : DevDenCommand(
    name = "leaderboard",
    help = "View the leaderboard",
    aliases = arrayOf("top", "lb")
) {

    override suspend fun CommandEvent.execute() {
        val args = args.split(WHITESPACE_REGEX)

        if (args.isEmpty() || args.firstOrNull()?.isEmpty() != false) {
            return sendLeaderboard(10, StatsUser::xp, "XP")
        }

        val firstArgAsInt = args.first().toIntOrNull()
        val count = when {
            firstArgAsInt != null -> {
                val parsedCount = args.first().toInt()
                if (parsedCount > 10) {
                    reply("I can only show up to 10 entries in the leaderboard")
                    return
                }
                parsedCount
            }
            else -> 10
        }


        val leaderboardBy = when {
            firstArgAsInt == null -> {
                args.first()
            }
            args.size == 1 -> "xp"
            else -> args[1]
        }.toLowerCase()

        val property = when (leaderboardBy) {
            "xp" -> StatsUser::xp
            else -> {
                reply("Hmm, I don't recognise $leaderboardBy")
                return
            }
        }

        sendLeaderboard(count, property, leaderboardBy.capitalize())
    }

    private suspend fun CommandEvent.sendLeaderboard(
        count: Int,
        by: KProperty1<StatsUser, BigInteger>,
        byPrettyName: String,
    ) {
        val users = StatsUsers.all

        val sorted = users.sortedByDescending(by)
            .take(count)

        reply {
            title = "Top $count Users based on $byPrettyName"

            for ((index, elem) in sorted.withIndex()) {
                field(
                    "#${index + 1} - ${by(elem)} $byPrettyName",
                    jda.getUserById(elem.userId)?.asMention ?: "Unknown User (${elem.userId})",
                )
            }
        }
    }

}
