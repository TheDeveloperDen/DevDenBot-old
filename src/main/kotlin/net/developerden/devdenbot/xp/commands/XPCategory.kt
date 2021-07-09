package net.developerden.devdenbot.xp.commands

import net.developerden.devdenbot.commands.category.CommandCategory
import net.dv8tion.jda.api.entities.Activity

object XPCategory : CommandCategory(
    name = "XP",
    description = "XP Related Commands",
    emoji = Activity.Emoji("\uD83C\uDF89"),
    shortName = "xp"
)
