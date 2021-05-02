package me.bristermitten.devdenbot.xp.commands

import me.bristermitten.devdenbot.commands.category.CommandCategory
import net.dv8tion.jda.api.entities.Activity

object XPCategory : CommandCategory(
    name = "XP",
    description = "XP Related Commands",
    emoji = Activity.Emoji("\uD83C\uDF89"),
    shortName = "xp"
)
