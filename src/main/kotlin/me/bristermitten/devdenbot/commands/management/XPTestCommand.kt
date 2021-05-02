package me.bristermitten.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.xp.xpForMessage
import java.io.File
import javax.inject.Inject


/**
 * @author AlexL
 */
class XPTestCommand @Inject constructor(
    val config: DDBConfig
) : DevDenCommand(
    name = "xptest",
    help = "Find how much XP a message would give",
) {
    override suspend fun CommandEvent.execute() {
        val xp = xpForMessage(args)
        event.message.reply("This message would give **$xp** XP.").await()
    }


}
