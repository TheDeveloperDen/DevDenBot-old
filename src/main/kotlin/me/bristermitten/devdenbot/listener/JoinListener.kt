package me.bristermitten.devdenbot.listener

import com.google.inject.Inject
import me.bristermitten.devdenbot.serialization.DDBConfig
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

private const val welcomeChannelId = 821743171942744114

class JoinListener @Inject constructor(private val config: DDBConfig) : ListenerAdapter() {
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val channel = event.jda.getGuildChannelById(welcomeChannelId) as? TextChannel
        channel?.sendMessage("Welcome ${event.user.asMention} to the Developer's Den! There are now ${event.guild.memberCount} users.")
    }
}
