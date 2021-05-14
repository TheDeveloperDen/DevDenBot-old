package me.bristermitten.devdenbot.commands

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.util.hasRoleOrIsModerator

class PreconditionFailedException(val reason: String? = null) : Exception()

fun CommandEvent.senderMustHaveRole(roleId: Long) {
    if (hasRoleOrIsModerator(member, roleId)) {
        return
    }
    val roleName = jda.getRoleById(roleId) ?: error("Invalid role id $roleId")
    throw PreconditionFailedException("You must have the role `$roleName` to use this command!")
}

