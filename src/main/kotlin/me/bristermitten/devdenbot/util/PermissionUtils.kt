package me.bristermitten.devdenbot.util

import me.bristermitten.devdenbot.discord.MODERATOR_ROLE_ID
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member

fun hasRole(member: Member, roleId: Long) = member.roles.any { it.idLong == roleId }

fun hasRoleOrIsModerator(member: Member, roleId: Long) = isModerator(member) || hasRole(member, roleId)
fun isModerator(member: Member) = isAdmin(member) || member.hasPermission(Permission.MESSAGE_MANAGE) || hasRole(member, MODERATOR_ROLE_ID)
fun isAdmin(member: Member) = isOwner(member) || member.hasPermission(Permission.ADMINISTRATOR)
fun isOwner(member: Member) = member.isOwner

