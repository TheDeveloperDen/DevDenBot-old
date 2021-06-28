package me.bristermitten.devdenbot.xp.commands

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.extensions.commands.prepareReply
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.util.formatNumber
import me.bristermitten.devdenbot.xp.xpForLevel
import javax.inject.Inject
import kotlin.coroutines.resume


/**
 * @author AlexL
 */
@Used
class ProfileCommand @Inject constructor(
) : DevDenCommand(
    name = "profile",
    help = "View your profile",
    category = XPCategory,
) {

    companion object {
        val idRegex = Regex("""<@(\d+)>|(\d+)""")
    }

    override suspend fun CommandEvent.execute() {
        val args = event.message.contentRaw.removePrefix(client.prefix)
            .dropWhile { !it.isWhitespace() }

        // what is this monstrosity
        val targetUser = if (args.isNullOrBlank()) event.author else
            idRegex.matchEntire(args)?.groups?.first()
                ?.let {
                    guild.retrieveMemberById(it.value, false).await { cont, _ ->
                        cont.resume(null)
                    }
                }?.user
                ?: guild.retrieveMembersByPrefix(args, 1).await().firstOrNull()
                    ?.takeIf { it.user.name == args || it.nickname == args }
                    ?.user
                ?: event.author

        val statsUser = StatsUsers.get(targetUser.idLong)

        val action = prepareReply {
            title = "Your Statistics"
            field("XP", formatNumber(statsUser.xp), true)
            field("Level", statsUser.level.toString(), true)
            field("Disboard Bumps", statsUser.bumps.toString(), true)
            field("XP to Level", formatNumber(xpForLevel(statsUser.level + 1)), true)
            setFooter("Statistics for ${targetUser.name}")
        }

        action.await()
    }
}
