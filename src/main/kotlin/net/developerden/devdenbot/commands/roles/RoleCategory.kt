package net.developerden.devdenbot.commands.roles

import net.developerden.devdenbot.commands.category.CommandCategory
import net.dv8tion.jda.api.entities.Activity

object RoleCategory : CommandCategory(
    name = "Roles",
    description = "Self-assigning roles",
    emoji = Activity.Emoji("\uD83D\uDD34"),
    shortName = "role"
)
