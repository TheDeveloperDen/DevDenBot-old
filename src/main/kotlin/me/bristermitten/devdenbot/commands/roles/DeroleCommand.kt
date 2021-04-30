package me.bristermitten.devdenbot.commands.roles

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Role
import java.lang.NumberFormatException
import javax.inject.Inject

class DeroleCommand @Inject constructor(
        val jda : JDA
) : DevDenCommand(
        name = "derole",
        help = "Remove your own role",
        aliases = arrayOf()
) {

    override suspend fun CommandEvent.execute() {
        val roles = jda.getRolesByName(args, true)
        val role = roles.firstOrNull { RoleCommand.ROLES.contains(it.idLong) }
        if (role == null) {
            channel.sendMessage("Invalid role!").queue()
            return
        }
        if (message.guild.getMember(author)?.roles?.contains(role) == false) {
            channel.sendMessage("You don't have this role!").queue()
            return
        }
        message.guild.removeRoleFromMember(author.idLong, role).queue()
        channel.sendMessage("Role removed!").queue()
    }

}