package me.bristermitten.devdenbot.commands.info

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.pasting.HasteClient
import net.dv8tion.jda.api.JDA
import javax.inject.Inject

@Used
class PasteCommand @Inject constructor(
    val jda: JDA,
) : DevDenCommand(
    name = "paste",
    help = "Show the paste link",
    category = InfoCategory,
    aliases = arrayOf(),
    commandChannelOnly = false
) {

    override suspend fun CommandEvent.execute() {
        message.channel.sendMessage(HasteClient.baseUrl).await()
    }

}
