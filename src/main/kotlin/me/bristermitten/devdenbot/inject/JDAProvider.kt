package me.bristermitten.devdenbot.inject

import club.minnced.jda.reactor.ReactiveEventManager
import com.google.inject.Inject
import com.google.inject.Provider
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import me.bristermitten.devdenbot.listener.EventListener
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.log
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent

/**
 * @author AlexL
 */
class JDAProvider @Inject constructor(
    private val commandClient: CommandClient,
    private val config: DDBConfig,
    private val eventWaiter: EventWaiter,
    private val listeners: Set<EventListener>,
) : Provider<JDA> {
    private val log by log()

    override fun get(): JDA {
        val manager = ReactiveEventManager()
        val jda = JDABuilder.createDefault(config.token)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .addEventListeners(commandClient, eventWaiter)
            .setEventManager(manager)
            .build()

        listeners.forEach {
            log.debug { "Registering listener $it" }
            it.register(jda)
        }

        return jda
    }
}
