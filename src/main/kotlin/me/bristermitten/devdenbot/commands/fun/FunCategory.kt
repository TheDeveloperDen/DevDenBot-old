package me.bristermitten.devdenbot.commands.info

import me.bristermitten.devdenbot.commands.category.CommandCategory
import net.dv8tion.jda.api.entities.Activity

object FunCategory : CommandCategory(
        name = "Fun",
        description = "Fun commands",
        emoji = Activity.Emoji("\uD83C\uDFA2"),
        shortName = "fun"
)
