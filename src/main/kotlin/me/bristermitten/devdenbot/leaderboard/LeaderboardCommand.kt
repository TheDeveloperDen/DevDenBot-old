package me.bristermitten.devdenbot.leaderboard

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.extensions.WHITESPACE_REGEX
import me.bristermitten.devdenbot.extensions.commands.awaitReply
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.util.mention
import me.bristermitten.devdenbot.xp.commands.XPCategory
import java.util.*
import javax.inject.Inject


/**
 * @author AlexL
 */
@Used
class LeaderboardCommand @Inject constructor(
    private val eventWaiter: EventWaiter,
) : DevDenCommand(
    name = "leaderboard",
    help = "View the leaderboard",
    category = XPCategory,
    aliases = arrayOf("top", "lb")
) {
    private companion object {
        const val DEFAULT_LEADERBOARD_COUNT = 10
    }


    override suspend fun CommandEvent.execute() {
        val args = args.split(WHITESPACE_REGEX)

        if (args.isEmpty() || args.firstOrNull()?.isEmpty() != false) {
            return sendLeaderboard(10, Leaderboards.XP)
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
        }.lowercase(Locale.getDefault())

        val leaderboard = when (leaderboardBy) {
            "xp" -> Leaderboards.XP
            "level", "lvl" -> Leaderboards.LEVEL
            "bump", "bumps" -> Leaderboards.BUMPS
            else -> {
                awaitReply("Hmm, I don't recognise $leaderboardBy")
                return
            }
        }

        sendLeaderboard(count, leaderboard)
    }

    private fun <C : Comparable<C>> CommandEvent.sendLeaderboard(
        entriesPerPage: Int,
        statsUserLeaderboard: StatsUserLeaderboard<C>,
    ) {
        DevDenPaginator(
            { statsUserLeaderboard.getEntry(it) },
            { builder, statsUser, index ->
                builder.field(
                    "#${index + 1} - ${statsUserLeaderboard.keyExtractor(statsUser)} ${statsUserLeaderboard.name}",
                    mention(statsUser.id.value)
                )
            },
            entryCount = statsUserLeaderboard.getEntryCount(),
            title = "Users ranked by ${statsUserLeaderboard.name}",
            eventWaiter = eventWaiter,
            entriesPerPage = entriesPerPage,
        ).display(channel)
    }

}
