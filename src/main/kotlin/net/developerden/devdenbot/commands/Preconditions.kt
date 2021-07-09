package net.developerden.devdenbot.commands

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.arguments.Arguments
import net.developerden.devdenbot.util.hasRoleOrIsModerator

class PreconditionFailedException(val reason: String? = null) : Exception()

fun CommandEvent.senderMustHaveRole(roleId: Long) {
    if (hasRoleOrIsModerator(member, roleId)) {
        return
    }
    val roleName = jda.getRoleById(roleId) ?: error("Invalid role id $roleId")
    throw PreconditionFailedException("You must have the role `$roleName` to use this command!")
}


fun Arguments.requireLength(command: DevDenCommand, len: Int) {
    if (args.size != len) {
        throw PreconditionFailedException("Invalid argument length. Usage: `${command.arguments}`")
    }
}

fun Arguments.requireLengthAtLeast(command: DevDenCommand, len: Int) {
    if (args.size < len) {
        throw PreconditionFailedException("Not enough arguments. Usage: `${command.arguments}`")
    }
}
