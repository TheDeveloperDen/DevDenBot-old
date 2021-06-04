package me.bristermitten.devdenbot.commands.info

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.extensions.commands.firstMentionedUser
import me.bristermitten.devdenbot.inject.Used
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import org.apache.commons.lang3.StringUtils
import javax.inject.Inject


@Used
class PpCommand @Inject constructor(
        val jda: JDA,
) : DevDenCommand(
        name = "pp",
        help = "Show the size of a user's pp",
        category = FunCategory,
        aliases = arrayOf("penis"),
        commandChannelOnly = false
) {

    override suspend fun CommandEvent.execute() {

        if(args[0].equals(null)) { message.channel.sendMessage("Please specify args.").await(); return }

        val user = firstMentionedUser() ?: jda.getUserById(args[0].toString())

        if(user == null) { message.channel.sendMessage("Unable to find that user.").await(); return }

        val length = StringUtils.repeat('=', (0..16).random())

        message.channel.sendMessage(EmbedBuilder()
            .setAuthor("PP Size Detector")
            .setDescription("${user.name}'s penis:\n8${length}")
            .build()).await()
    }
}
