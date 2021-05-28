package me.bristermitten.devdenbot.listener

import me.bristermitten.devdenbot.discord.WELCOME_CHANNEL_ID
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent


@Used
class JoinListener : ReflectiveEventListener() {
    @Used
    suspend fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val channel = event.jda.getTextChannelById(WELCOME_CHANNEL_ID)
        channel?.sendMessage("Welcome ${event.user.asMention} to the Developer's Den! There are now ${event.guild.memberCount} users.")
            ?.await()
    }

}
