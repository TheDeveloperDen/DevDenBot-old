package me.bristermitten.devdenbot.xp.category

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.WHITESPACE_REGEX
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.extensions.commands.awaitReply
import me.bristermitten.devdenbot.extensions.commands.reply
import net.dv8tion.jda.api.JDA
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
    private companion object {
        const val DEFAULT_LEADERBOARD_COUNT = 10
    }

    override suspend fun CommandEvent.execute() {
        val args = args.split(WHITESPACE_REGEX)

        if (args.isEmpty() || args.firstOrNull()?.isEmpty() != false) {
            return sendLeaderboard(10, StatsUser::xp as KProperty1<StatsUser, Comparable<Any>>, "XP")
        }

        val firstArgAsInt = args.first().toIntOrNull()
        val count = when {
            firstArgAsInt != null -> {
                val parsedCount = args.first().toInt()
                if (parsedCount > DEFAULT_LEADERBOARD_COUNT) {
                    reply("I can only show up to $DEFAULT_LEADERBOARD_COUNT entries in the leaderboard")
                    return
                }
                parsedCount
            }
            else -> DEFAULT_LEADERBOARD_COUNT
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
            "level", "lvl" -> StatsUser::level
            else -> {
                awaitReply("Hmm, I don't recognise $leaderboardBy")
                return
            }
        }

        sendLeaderboard(count, property as KProperty1<StatsUser, Comparable<Any>>, leaderboardBy.capitalize())
    }

    private suspend fun <C: Comparable<Any>> CommandEvent.sendLeaderboard(
        count: Int,
        by: KProperty1<StatsUser, C>,
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
                    jda.retrieveUserById(elem.userId).await()?.asMention ?: "Unknown User (${elem.userId})",
                )
            }
        }
    }

}
