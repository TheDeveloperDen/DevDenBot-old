package me.bristermitten.devdenbot.commands.management

import me.bristermitten.devdenbot.commands.category.CommandCategory
import net.dv8tion.jda.api.entities.Activity

object ManagingCategory : CommandCategory(
    name = "Management",
    description = "General config and help commands",
    emoji = Activity.Emoji("\uD83D\uDD27"),
    shortName = "manage"
)
