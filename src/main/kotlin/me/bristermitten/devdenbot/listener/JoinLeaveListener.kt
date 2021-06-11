package me.bristermitten.devdenbot.listener

import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.util.handleEachIn
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
import me.bristermitten.devdenbot.discord.WELCOME_CHANNEL_ID
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent


@Used
class JoinLeaveListener : EventListener {
    private suspend fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val channel = event.jda.getTextChannelById(WELCOME_CHANNEL_ID)
        channel?.sendMessage("Welcome ${event.user.asMention} to the Developer's Den! There are now ${event.guild.memberCount} users.")
            ?.await()
    }

    private suspend fun onGuildMemberLeave(event: GuildMemberRemoveEvent) {
        val channel = event.jda.getTextChannelById(WELCOME_CHANNEL_ID)
        channel?.sendMessage("${event.user.asMention} has left \\:(. There are now ${event.guild.memberCount} users.")
            ?.await()
    }

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMemberJoinEvent>().handleEachIn(scope, this::onGuildMemberJoin)
        jda.listenFlow<GuildMemberRemoveEvent>().handleEachIn(scope, this::onGuildMemberLeave)
    }
}
