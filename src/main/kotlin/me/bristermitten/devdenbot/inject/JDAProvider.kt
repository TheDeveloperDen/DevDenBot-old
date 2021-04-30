package me.bristermitten.devdenbot.inject

import club.minnced.jda.reactor.ReactiveEventManager
import com.google.inject.Inject
import com.google.inject.Provider
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import me.bristermitten.devdenbot.serialization.DDBConfig
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter

/**
 * @author AlexL
 */
class JDAProvider @Inject constructor(
    private val commandClient: CommandClient,
    private val config: DDBConfig,
    private val eventWaiter: EventWaiter,
    private val listeners: Set<ListenerAdapter>
) : Provider<JDA> {

    override fun get(): JDA {
        val manager = ReactiveEventManager()
        return JDABuilder.createDefault(config.token)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .addEventListeners(commandClient, eventWaiter, *listeners.toTypedArray())
            .setEventManager(manager)
            .build()
    }
}
