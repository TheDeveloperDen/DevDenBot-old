package me.bristermitten.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.serialization.DDBConfig
import javax.inject.Inject


/**
 * @author AlexL
 */
@Used
class ReloadCommand @Inject constructor(
    val config: DDBConfig,
) : DevDenCommand(
    name = "reload",
    help = "Reload all data",
    ownerCommand = true
) {

    override suspend fun CommandEvent.execute() {
        reply("currently borked soz")
    }


}
