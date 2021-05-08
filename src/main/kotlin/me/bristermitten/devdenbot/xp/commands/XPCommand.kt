package me.bristermitten.devdenbot.xp.commands

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.extensions.commands.firstMentionedUser
import me.bristermitten.devdenbot.extensions.commands.prepareReply
import me.bristermitten.devdenbot.graphics.createTextImage
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.formatNumber
import java.awt.Color
import java.awt.LinearGradientPaint
import javax.inject.Inject


/**
 * @author AlexL
 */
@Used
class XPCommand @Inject constructor(
    val config: DDBConfig,
) : DevDenCommand(
    name = "xp",
    help = "View the xp of a user",
    cooldown = 2,
    aliases = arrayOf("experience", "my-xp", "lvl")
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
        val targetUser = firstMentionedUser() ?: event.message.author
        val targetStatsUser = StatsUsers[targetUser.idLong]
        val text = formatNumber(targetStatsUser.xp.get()) + " XP"

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
