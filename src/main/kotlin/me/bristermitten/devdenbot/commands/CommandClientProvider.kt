package me.bristermitten.devdenbot.commands

import com.google.inject.Inject
import com.google.inject.Provider
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandClientBuilder
import me.bristermitten.devdenbot.data.languages
import me.bristermitten.devdenbot.serialization.DDBConfig
import net.dv8tion.jda.api.entities.Activity

/**
 * @author AlexL
 */
class CommandClientProvider @Inject constructor(private val config: DDBConfig,) :
    Provider<CommandClient> {

    override fun get(): CommandClient {
        val commandClient = CommandClientBuilder()
            .setOwnerId(config.ownerID.toString())
            .setPrefix(config.prefix)
            .setAlternativePrefix("@mention")
            .setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "Coding in ${languages.random()}"))
            .useHelpBuilder(true)
            .setHelpWord("")
            .setHelpConsumer {
                it.client.commands.firstOrNull { cmd ->
                    cmd.name == "help"
                }?.run(it)
            }



        return commandClient.build()
    }
}
