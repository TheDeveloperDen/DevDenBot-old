package me.bristermitten.devdenbot.commands.`fun`

import me.bristermitten.devdenbot.commands.category.CommandCategory
import net.dv8tion.jda.api.entities.Activity

object FunCategory : CommandCategory(
    "Fun",
    emoji = Activity.Emoji("\uD83C\uDF88"),
    description = "All sorts of fun and games!"
)
