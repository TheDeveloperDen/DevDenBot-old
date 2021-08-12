package net.developerden.devdenbot.xp.commands

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.getUser
import net.developerden.devdenbot.extensions.commands.prepareReply
import net.developerden.devdenbot.graphics.createTextImage
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.developerden.devdenbot.util.formatNumber
import java.awt.Color
import java.awt.LinearGradientPaint
import javax.inject.Inject


/**
 * @author AlexL
 */
@Used
class XPCommand @Inject constructor(
    private val config: DDBConfig,
) : DevDenCommand(
    name = "xp",
    help = "View the xp of a user",
    cooldown = 2,
    aliases = arrayOf("experience", "my-xp", "lvl"),
    category = XPCategory
) {

    companion object {
        private val gradient = LinearGradientPaint(
            0f, 0f, 0f, 100f,
            floatArrayOf(.2f, .6f),
            arrayOf(
                Color.decode("0xA933DC"),
                Color.decode("0xFFA500")
            )
        )
    }

    override suspend fun CommandEvent.execute() {
        val targetUser = getUser() ?: event.author
        val targetStatsUser = StatsUsers.get(targetUser.idLong)
        val text = formatNumber(targetStatsUser.xp) + " XP"

        val photo = createTextImage(width = 400,
            height = 200,
            fontSize = 80,
            text = text,
            fontColor = gradient,
            backgroundColor = Color.darkGray)
        val message = prepareReply {
            title = "XP of ${targetUser.name}#${targetUser.discriminator}"
            setColor(config.colour)
            setFooter("Developer Den", targetUser.effectiveAvatarUrl)

            setImage("attachment://xp.png")
        }

        message.addFile(photo, "xp.png")

        message.await()
    }
}
