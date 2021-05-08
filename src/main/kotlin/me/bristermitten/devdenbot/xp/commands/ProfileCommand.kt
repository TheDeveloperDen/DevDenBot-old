package me.bristermitten.devdenbot.xp.commands

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.extensions.commands.prepareReply
import me.bristermitten.devdenbot.inject.Used
import javax.inject.Inject


/**
 * @author AlexL
 */
@Used
class ProfileCommand @Inject constructor(
) : DevDenCommand(
    name = "profile",
    help = "View your profile"
) {

    override suspend fun CommandEvent.execute() {
        val targetUser = event.message.mentionedMembers.firstOrNull()?.user ?: event.message.author
        val statsUser = StatsUsers[targetUser.idLong]

        val action = prepareReply {
            title = "Your Statistics"
            field("XP", statsUser.xp.toString(), true)
            field("Level", statsUser.level.toString(), true)
            field("Disboard Bumps", statsUser.bumps.toString(), true)

            setFooter("Statistics for ${targetUser.name}")
        }

        action.await()
    }
}
