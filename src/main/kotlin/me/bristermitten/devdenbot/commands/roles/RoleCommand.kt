package me.bristermitten.devdenbot.commands.roles

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.extensions.await
import net.dv8tion.jda.api.JDA
import javax.inject.Inject

class RoleCommand @Inject constructor(
    val jda: JDA,
) : DevDenCommand(
    name = "role",
    help = "Give yourself a role",
    category = RoleCategory,
    aliases = arrayOf()
) {

    companion object {
        val ROLES: Set<Long> = setOf(
            837576267922538516L,
            837576282454622218L,
            837584481526874153L,
            831987774499454997L
        )
    }

    override suspend fun CommandEvent.execute() {
        val roles = jda.getRolesByName(args, true)
        val role = roles.firstOrNull { ROLES.contains(it.idLong) }
        if (role == null) {
            channel.sendMessage("Invalid role!").await()
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
