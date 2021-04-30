package me.bristermitten.devdenbot.commands.roles

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Role
import java.lang.NumberFormatException
import javax.inject.Inject

class RoleCommand @Inject constructor(
        val jda : JDA
) : DevDenCommand(
        name = "role",
        help = "Give yourself a role",
        aliases = arrayOf()
) {

    companion object {
        val ROLES: Set<Long> = setOf(
                837576267922538516L,
                837576282454622218L,
                837584481526874153
        )
    }

    override suspend fun CommandEvent.execute() {
        val roles = jda.getRolesByName(args, true)
        val role = roles.firstOrNull { ROLES.contains(it.idLong) }
        if (role == null) {
            channel.sendMessage("Invalid role!").queue()
            return
        }
        if (message.guild.getMember(author)?.roles?.contains(role) == true) {
            channel.sendMessage("You already have this role!").queue()
            return
        }
        message.guild.addRoleToMember(author.idLong, role).queue()
        channel.sendMessage("Role added!").queue()
    }

}