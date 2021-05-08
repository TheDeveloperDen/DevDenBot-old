package me.bristermitten.devdenbot.commands

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission

class PreconditionFailedException(val reason: String? = null) : Exception()

fun CommandEvent.senderMustHaveRole(roleId: Long) {
    if (member.isOwner || member.hasPermission(Permission.ADMINISTRATOR)) {
        return
    }

    if (member.roles.none { it.idLong == roleId }) {
        val roleName = jda.getRoleById(roleId) ?: error("Invalid role id $roleId")
        throw PreconditionFailedException("You must have the role `$roleName` to use this command!")
    }
}
