package net.developerden.devdenbot.commands.roles

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.discord.SELF_ROLES
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.util.getSuggestion
import net.dv8tion.jda.api.JDA
import javax.inject.Inject

@Used
class RoleCommand @Inject constructor(
    val jda: JDA,
) : DevDenCommand(
    name = "role",
    help = "Give yourself a role",
    category = RoleCategory,
    aliases = arrayOf()
) {

    override suspend fun CommandEvent.execute() {
        if (args == ""){
            channel.sendMessage("Invalid use of the role command: Please specify a role.")
            return
        }
        val roles = jda.getRolesByName(args, true)
        val role = roles.firstOrNull { SELF_ROLES.contains(it.idLong) }
        if (role == null) {
            val suggestion = getSuggestion(
                args,
                SELF_ROLES.mapNotNull { jda.getRoleById(it.toString())?.name })
                ?.let { " Do you mean '$it'?" } ?: ""
            channel.sendMessage("Invalid role!$suggestion").await()
            return
        }
        if (message.guild.getMember(author)?.roles?.contains(role) == true) {
            channel.sendMessage("You already have this role!").await()
            return
        }
        message.guild.addRoleToMember(author.idLong, role).await()
        channel.sendMessage("Role added!").await()
    }

}
