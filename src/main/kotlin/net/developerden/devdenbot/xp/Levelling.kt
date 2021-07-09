package net.developerden.devdenbot.xp

import net.developerden.devdenbot.discord.BOT_COMMANDS_CHANNEL_ID
import net.developerden.devdenbot.discord.getPing
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.extensions.commands.embedDefaults
import net.developerden.devdenbot.trait.HasConfig
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel

private val log = KotlinLogging.logger("Levelling")

suspend fun processLevelUp(user: Member, level: Int) {
    val channel = user.jda.getGuildChannelById(BOT_COMMANDS_CHANNEL_ID) as? TextChannel ?: return
    channel.sendMessage(
        """
            ${user.getPing()}, you levelled up to level **$level**!
            
            Don't want to be pinged? `ddrole No Ping`
            """.trimIndent()
    ).await()
    val tier = tierOf(level)
    val tierRole = tierRole(user.jda, tier)
    if (tierRole !in user.roles) {
        if (tier - 1 != 0) { //We can't remove @everyone
            val oldTier = tierRole(user.jda, tier - 1)
            user.guild.removeRoleFromMember(user, oldTier).await()
        }
        user.guild.addRoleToMember(user, tierRole).await()
    }
    log.trace { "Processed level up for ${user.user.name}" }
}
