package me.bristermitten.devdenbot.commands.roles

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.extensions.await
import net.dv8tion.jda.api.JDA
import javax.inject.Inject

class DeroleCommand @Inject constructor(
    val jda: JDA,
) : DevDenCommand(
    name = "derole",
    help = "Remove your own role",
    category = RoleCategory,
    aliases = arrayOf()
) {

    override suspend fun CommandEvent.execute() {
        val roles = jda.getRolesByName(args, true)
        val role = roles.firstOrNull { ROLES.contains(it.idLong) }
        if (role == null) {
            channel.sendMessage("Invalid role!").await()
            return
        }
        if (message.guild.getMember(author)?.roles?.contains(role) == false) {
            channel.sendMessage("You don't have this role!").await()
            return
        }
        message.guild.removeRoleFromMember(author.idLong, role).await()
        channel.sendMessage("Role removed!").await()
    }

}
