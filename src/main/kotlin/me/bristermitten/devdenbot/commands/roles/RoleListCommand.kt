package me.bristermitten.devdenbot.commands.roles

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.extensions.await
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import javax.inject.Inject

class RoleListCommand @Inject constructor(
    val jda: JDA,
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
        val builder = MessageBuilder()
            .append("""```
                $roleList
                ```""")
            .setEmbed(EmbedBuilder()
                .setTitle("Role Menu")
                .build()
            )

        channel.sendMessage(builder.build()).await()
    }

}
