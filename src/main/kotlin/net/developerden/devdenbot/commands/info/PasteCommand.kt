package net.developerden.devdenbot.commands.info

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.pasting.HasteClient
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
