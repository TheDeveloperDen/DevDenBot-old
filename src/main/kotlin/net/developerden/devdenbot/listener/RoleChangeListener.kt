package net.developerden.devdenbot.listener

import net.developerden.devdenbot.data.StatsUsers
import net.developerden.devdenbot.discord.BORDER_ROLES
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.util.handleEachIn
import net.developerden.devdenbot.util.isBelow
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.scope
import net.developerden.devdenbot.xp.TIER_ROLE_IDS
import net.developerden.devdenbot.xp.tierOf
import net.developerden.devdenbot.xp.tierRole
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent


@Used
class RoleChangeListener : EventListener {

    companion object {
        suspend fun update(member: Member) {
            val toAdd = mutableListOf<Role>()
            val toRemove = mutableListOf<Role>()
            BORDER_ROLES.mapNotNull(member.guild::getRoleById)
                .forEach { borderRole ->
                    if (member.user.isBot.not() && member.roles.any { it.isBelow(borderRole) }) {
                        toAdd.add(borderRole)
                    }
                    if (member.user.isBot || member.roles.none { it.isBelow(borderRole) }) {
                        toRemove.add(borderRole)
                    }
                }

            val stats = StatsUsers.get(member.idLong)
            val tierRole = tierRole(member.jda, tierOf(stats.level))
            TIER_ROLE_IDS
                .takeWhile { it != tierRole.idLong }
                .mapNotNull(member.guild::getRoleById)
                .forEach(toRemove::add) // Remove any previous tiers that might be leftover somehow


            member.guild.modifyMemberRoles(member, toAdd, toRemove)
        }
    }


    private suspend fun onUpdate(event: GuildMemberUpdateEvent) = update(event.member)


    override fun register(jda: JDA) {
        jda.listenFlow<GuildMemberUpdateEvent>().handleEachIn(scope, this::onUpdate)
    }
}
