package me.bristermitten.devdenbot.stats

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.listener.EventListener
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

@Used
class StatsListener : EventListener {

    private fun onGuildMessageReceived() {
//        GlobalStats.totalMessagesSent++ TODO implement
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>()
            .onEach { this.onGuildMessageReceived() }
            .launchIn(scope)
    }
}
