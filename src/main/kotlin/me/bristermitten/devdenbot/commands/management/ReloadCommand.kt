package me.bristermitten.devdenbot.commands.management

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.serialization.DDBConfig
import java.io.File
import javax.inject.Inject


/**
 * @author AlexL
 */
@Used
class ReloadCommand @Inject constructor(
    val config: DDBConfig
) : DevDenCommand(
    name = "reload",
    help = "Reload all data",
    ownerCommand = true
) {

    override suspend fun CommandEvent.execute() {
        val statsFile = File("/var/data/stats.json")
        if (!statsFile.exists()) {
            return
        }
        val content = statsFile.readText()
        StatsUsers.loadFrom(content)

        reply(":white_check_mark: Reloaded.")
    }


}
