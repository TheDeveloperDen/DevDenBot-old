package net.developerden.devdenbot.commands.roles

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.commands.DevDenCommand
import net.developerden.devdenbot.discord.SELF_ROLES
import net.developerden.devdenbot.extensions.await
import net.developerden.devdenbot.inject.Used
import net.developerden.devdenbot.serialization.DDBConfig
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import javax.inject.Inject

@Used
class RoleListCommand @Inject constructor(
    val jda: JDA,
    val config: DDBConfig
) : DevDenCommand(
    name = "roles",
    help = "Show all available roles",
    category = RoleCategory,
    aliases = arrayOf("rolelist")
) {

    override suspend fun CommandEvent.execute() {
        val roleList = SELF_ROLES
            .mapNotNull { jda.getRoleById(it) }
            .joinToString("\n") { ">> " + it.name }
        val builder = EmbedBuilder()
            .setTitle("Role Menu")
            .setDescription("```$roleList```")
            .setColor(config.colour)
            .setFooter("${config.prefix}role <role> to add a role")
            .build()

        reply(builder)
    }

}
