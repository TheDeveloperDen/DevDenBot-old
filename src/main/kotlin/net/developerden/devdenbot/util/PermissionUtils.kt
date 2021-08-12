package net.developerden.devdenbot.util

import net.developerden.devdenbot.discord.MODERATOR_ROLE_ID
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role

fun hasRole(member: Member, roleId: Long) = member.roles.any { it.idLong == roleId }

fun hasRoleOrIsModerator(member: Member, roleId: Long) = isModerator(member) || hasRole(member, roleId)

fun Member.hasRoleOrAbove(roleId: Long) = hasRole(this, roleId) || jda.getRoleById(roleId)?.let { role ->
    roles.any { ourRole ->
        ourRole.isAbove(role)
    }
} == true

fun Role.isBelow(role: Role) = position < role.position
 fun Role.isAbove(role: Role) = position > role.position

fun isModerator(member: Member) =
    isAdmin(member) || member.hasPermission(Permission.MESSAGE_MANAGE) || hasRole(member, MODERATOR_ROLE_ID)

fun isAdmin(member: Member) = isOwner(member) || member.hasPermission(Permission.ADMINISTRATOR)
fun isOwner(member: Member) = member.isOwner

