package me.bristermitten.devdenbot.commands.xp

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.extensions.commands.prepareReply
import javax.inject.Inject


/**
 * @author AlexL
 */
class ProfileCommand @Inject constructor(
) : DevDenCommand(
    name = "profile",
    help = "View your profile",
    aliases = arrayOf("stats")
) {

    override suspend fun CommandEvent.execute() {
        val targetUser = event.message.mentionedMembers.firstOrNull()?.user ?: event.message.author
        val statsUser = StatsUsers[targetUser.idLong]

//        val photo = makeJeevesPhoto(targetUser.effectiveAvatarUrl)

        val action = prepareReply {
            title = "Your Statistics"
            field("XP", statsUser.xp.toString(), false)

            setFooter("Statistics for ${targetUser.name}")
//            setImage("attachment://profile.png")
        }
//        action.addFile(photo, "profile.png")

        action.await()
    }
//    private suspend fun makeJeevesPhoto(personUrl: String): ByteArray = withContext(Dispatchers.IO) {
//        val image = javaClass.classLoader.getResource("jeeves.png")
//        val bufferedImage = ImageIO.read(image)
//        val graphics2D = bufferedImage.createGraphics()
//
//        graphics2D.drawImage(bufferedImage, 0, 0, null)
//
//        val url = URL(personUrl)
//        val connection = url.openConnection()
//        connection.addRequestProperty("User-Agent", "DevDen")
//
//        val profile = ImageIO.read(connection.getInputStream())
//
//        graphics2D.drawImage(profile, 290, 110, null)
//        graphics2D.dispose()
//
//        val at = AffineTransform()
//        at.scale(0.5, 0.5)
//        val scaleOp = AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
//
//        var scaledImage = BufferedImage(bufferedImage.width / 2, bufferedImage.height / 2, BufferedImage.TYPE_INT_ARGB)
//        scaledImage = scaleOp.filter(bufferedImage, scaledImage)
//
//
//        scaledImage.toPNG()
//    }
}
