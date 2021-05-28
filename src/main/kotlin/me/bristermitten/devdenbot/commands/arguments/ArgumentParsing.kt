package me.bristermitten.devdenbot.commands.arguments

import com.jagrosh.jdautilities.command.CommandEvent

fun CommandEvent.arguments(): Arguments {
    return parseArguments(client.prefix, message.contentRaw)!!
    //should never be null, command client already validated it's a valid command
}

fun parseArguments(prefix: String, content: String): Arguments? {
    if (!content.startsWith(prefix)) {
        return null
    }
    val tokens = ArgumentLexer.lex(content.removePrefix(prefix))
    return ArgumentParser.parse(tokens)
}


