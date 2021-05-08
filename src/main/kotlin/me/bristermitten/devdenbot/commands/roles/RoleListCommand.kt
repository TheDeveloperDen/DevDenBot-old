package me.bristermitten.devdenbot.commands.roles

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.inject.Used
import me.bristermitten.devdenbot.serialization.DDBConfig
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
        val roleList = ROLES
            .mapNotNull { jda.getRoleById(it) }
            .joinToString("\n") { ">> " + it.name }
        val builder = EmbedBuilder()
            .setTitle("Role Menu")
            .setDescription("```$roleList```")
            .setColor(config.colour)
            .setFooter("ddrole <role> to add a role")
            .build()

        channel.sendMessage(builder).await()
    }

}
