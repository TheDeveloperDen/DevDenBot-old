package me.bristermitten.devdenbot.listener

import com.google.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
import me.bristermitten.devdenbot.util.welcomeChannelId
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent


class JoinListener @Inject constructor(private val config: DDBConfig) : EventListener {
    private suspend fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val channel = event.jda.getTextChannelById(welcomeChannelId)
        channel?.sendMessage("Welcome ${event.user.asMention} to the Developer's Den! There are now ${event.guild.memberCount} users.")
            ?.await()
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMemberJoinEvent>().onEach { onGuildMemberJoin(it) }.launchIn(scope)
    }
}
