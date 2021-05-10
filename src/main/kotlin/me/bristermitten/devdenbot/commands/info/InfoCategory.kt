package me.bristermitten.devdenbot.commands.info

import me.bristermitten.devdenbot.commands.category.CommandCategory
import net.dv8tion.jda.api.entities.Activity

object InfoCategory : CommandCategory(
    name = "Info",
    description = "Miscellaneous information",
    emoji = Activity.Emoji("\uD83D\uDD34"),
    shortName = "info"
)
