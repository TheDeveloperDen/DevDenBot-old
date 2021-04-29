package me.bristermitten.devdenbot.commands.category

import com.jagrosh.jdautilities.command.Command
import net.dv8tion.jda.api.entities.Activity

/**
 * @author Alexander Wood (BristerMitten)
 */
open class CommandCategory(
    name: String,
    val shortName: String = name.lowercase(),
    val emoji: Activity.Emoji,
    val description: String
) : Command.Category(name) {
    init {
        @Suppress("LeakingThis")
        Categories.register(this)
    }
}
