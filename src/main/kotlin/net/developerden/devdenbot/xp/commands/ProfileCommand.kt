package net.developerden.devdenbot.xp.commands

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.prepareReply
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.util.formatNumber
import net.developerden.devdenbot.xp.xpForLevel
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
        val idRegex = Regex("""<@!?(\d+)>""")
    }

    override suspend fun CommandEvent.execute() {
        val args = event.message.contentRaw.removePrefix(client.prefix)
            .dropWhile { !it.isWhitespace() }.trimStart()

        // what is this monstrosity
        val targetUser = if (args.isBlank()) event.author else
            idRegex.matchEntire(args)?.groups?.get(1)
                ?.let {
                    guild.retrieveMemberById(it.value, false).await { cont, _ ->
                        cont.resume(null)
                    }
                }?.user
                ?: guild.retrieveMembersByPrefix(args, 1).await()
                    .firstOrNull()
                    ?.takeIf { it.user.name == args || it.nickname == args }
                    ?.user

        if (targetUser == null){
            channel.sendMessage("Unknown user: $args").queue().also { return }
        }

        val statsUser = StatsUsers.get(targetUser!!.idLong)

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
