package me.bristermitten.devdenbot.listener

import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.util.handleEachIn
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
import me.bristermitten.devdenbot.util.welcomeChannelId
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent


@Used
class JoinListener : EventListener {
    private suspend fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val channel = event.jda.getTextChannelById(welcomeChannelId)
        channel?.sendMessage("Welcome ${event.user.asMention} to the Developer's Den! There are now ${event.guild.memberCount} users.")
            ?.await()
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMemberJoinEvent>().handleEachIn(scope, this::onGuildMemberJoin)
    }
}
