package net.developerden.devdenbot.commands.roles

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.discord.SELF_ROLES
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.inject.Used
import net.dv8tion.jda.api.JDA
import javax.inject.Inject

@Used
class DeroleCommand @Inject constructor(
    val jda: JDA,
) : DevDenCommand(
    name = "derole",
    help = "Remove your own role",
    category = RoleCategory,
    aliases = arrayOf("roleremove", "removerole", "unrole")
) {

    override suspend fun CommandEvent.execute() {
        val roles = jda.getRolesByName(args, true)
        val role = roles.firstOrNull { SELF_ROLES.contains(it.idLong) }
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
