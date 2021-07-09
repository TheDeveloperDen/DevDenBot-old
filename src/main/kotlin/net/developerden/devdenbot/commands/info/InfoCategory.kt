package net.developerden.devdenbot.commands.info

import net.developerden.devdenbot.commands.category.CommandCategory
import net.dv8tion.jda.api.entities.Activity

object InfoCategory : CommandCategory(
    name = "Info",
    description = "Miscellaneous information",
    emoji = Activity.Emoji("ℹ️"),
    shortName = "info"
)
