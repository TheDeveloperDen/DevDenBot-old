package net.developerden.devdenbot.listener

import net.developerden.devdenbot.discord.*
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.util.handleEachIn
import net.developerden.devdenbot.util.listenFlow
import net.developerden.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent


@Used
class RoleChangeListener : EventListener {

    companion object {
        suspend fun update(member: Member) {
            val toAdd = mutableListOf<Long>()
            val toRemove = mutableListOf<Long>()

            if (member.roles.any { it.idLong in GENERAL_ROLES }) {
                toAdd.add(GENERAL_BORDER_ROLE)
            } else {
                toRemove.add(GENERAL_BORDER_ROLE)
            }

            if (member.roles.any { it.idLong in TAGS_ROLES }) {
                toAdd.add(TAGS_BORDER_ROLE)
            } else {
                toRemove.add(TAGS_BORDER_ROLE)
            }
            if (member.roles.any { it.idLong in LANGUAGES_ROLES }) {
                toAdd.add(LANGUAGES_BORDER_ROLE)
            } else {
                toRemove.add(LANGUAGES_BORDER_ROLE)
            }

            member.guild.modifyMemberRoles(member, toAdd.mapNotNull(member.guild::getRoleById), toRemove.mapNotNull(member.guild::getRoleById))
                .await()

        }
    }


    private suspend fun onUpdate(event: GuildMemberUpdateEvent) = update(event.member)
    private suspend fun onRoleAdd(event: GuildMemberRoleAddEvent) = update(event.member)
    private suspend fun onRoleRemove(event: GuildMemberRoleRemoveEvent) = update(event.member)

    override fun register(jda: JDA) {
        jda.listenFlow<GuildMemberUpdateEvent>().handleEachIn(scope, this::onUpdate)
        jda.listenFlow<GuildMemberRoleAddEvent>().handleEachIn(scope, this::onRoleAdd)
        jda.listenFlow<GuildMemberRoleRemoveEvent>().handleEachIn(scope, this::onRoleRemove)
    }
}
