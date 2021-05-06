package me.bristermitten.devdenbot.xp.commands

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.jagrosh.jdautilities.menu.Paginator
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.WHITESPACE_REGEX
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.extensions.commands.awaitReply
import me.bristermitten.devdenbot.extensions.commands.reply
import me.bristermitten.devdenbot.leaderboard.DevDenPaginator
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.serialization.PrettyName
import net.dv8tion.jda.api.JDA
import java.awt.Color
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf


/**
 * @author AlexL
 */
class LeaderboardCommand @Inject constructor(
    private val eventWaiter: EventWaiter,
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
            return sendLeaderboard(10, StatsUser::xp, "XP")
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
            "bump", "bumps" -> StatsUser::bumps
            else -> {
                awaitReply("Hmm, I don't recognise $leaderboardBy")
                return
            }
        }

        val prettyName = property.findAnnotation<PrettyName>()?.prettyName ?: leaderboardBy.capitalize()
        val propertyType = property.returnType
        val func: (StatsUser) -> Comparable<Any> =
            when {
                propertyType.isSubtypeOf(Comparable::class.createType()) -> { it -> property(it) as Comparable<Any> }
                propertyType == AtomicInteger::class.createType() -> { it ->
                    (property(it) as AtomicInteger).get() as Comparable<Any>
                }
                else -> error("i want death")
            }

        sendLeaderboard(count, func, prettyName)
    }

    private suspend fun <C : Comparable<C>> CommandEvent.sendLeaderboard(
        entriesPerPage: Int,
        by: (StatsUser) -> C,
        leaderboardName: String,
    ) {
        val users = StatsUsers.all.sortedByDescending(by)

        DevDenPaginator(
            { users[it % users.size] }, //TODO replace with it
            { builder, statsUser, index ->
                builder.field(
                    "#${index + 1} - ${by(statsUser)} $leaderboardName",
                    jda.retrieveUserById(statsUser.userId).await()?.asMention ?: "Unknown User (${statsUser.userId})",
                )
            },
            entryCount = 40, //TODO replace with users.size
            title = "Users ranked by $leaderboardName",
            eventWaiter = eventWaiter,
            entriesPerPage = entriesPerPage,
        ).display(channel)
    }

}
