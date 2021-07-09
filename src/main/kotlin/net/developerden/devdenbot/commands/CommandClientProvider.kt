package net.developerden.devdenbot.commands

import com.google.inject.Inject
import com.google.inject.Provider
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandClientBuilder
import net.developerden.devdenbot.data.languages
import net.developerden.devdenbot.serialization.DDBConfig
import net.dv8tion.jda.api.entities.Activity

/**
 * @author AlexL
 */
class CommandClientProvider @Inject constructor(private val config: DDBConfig) :
    Provider<CommandClient> {

    override fun get(): CommandClient {
        val commandClient = CommandClientBuilder()
            .setOwnerId(config.ownerID.toString())
            .setPrefix(config.prefix)
            .setAlternativePrefix("@mention")
            .setActivity(Activity.playing("Coding in ${languages.random()} (${config.prefix}help)"))
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
