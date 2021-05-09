package me.bristermitten.devdenbot.commands

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member

class PreconditionFailedException(val reason: String? = null) : Exception()

fun CommandEvent.senderMustHaveRole(roleId: Long) {
    if (isAdmin(member)) {
        return
    }

    if (member.roles.none { it.idLong == roleId }) {
        val roleName = jda.getRoleById(roleId) ?: error("Invalid role id $roleId")
        throw PreconditionFailedException("You must have the role `$roleName` to use this command!")
    }
}

fun isAdmin(member: Member) = member.isOwner || member.hasPermission(Permission.ADMINISTRATOR)
