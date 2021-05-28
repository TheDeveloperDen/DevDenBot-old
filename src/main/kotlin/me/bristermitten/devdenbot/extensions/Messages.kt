package me.bristermitten.devdenbot.extensions

import com.jagrosh.jdautilities.command.CommandEvent

val WHITESPACE_REGEX = Regex("\\s+")

fun CommandEvent.arguments(): Arguments {
    return parseArguments(client.prefix,
        message.contentRaw)!! //should never be null, command client already validated it's a valid command
}

fun parseArguments(prefix: String, content: String): Arguments? {
    if (!content.startsWith(prefix)) {
        return null
    }
    val split = content.split(WHITESPACE_REGEX)
    val args = split.drop(1).map(::Argument)
    return Arguments(
        split.first()
            .removePrefix(prefix), //This might break in future for prefixes that don't require a space. deal with it
        args.filter { !it.isFlag },
        args.filter { it.isFlag })
}


class Arguments(val command: String, val args: List<Argument>, val flags: List<Argument>) {

    inline fun validateArgLength(length: Int, orElse: () -> Unit) {
        if (args.size < length)
            orElse()
    }

    override fun toString(): String {
        return "Arguments(command='$command', args=$args)"
    }

}


class Argument(unformattedContent: String) {
    val isFlag = unformattedContent.startsWith('-')
    val content = unformattedContent.removePrefix("-")

    inline fun validate(predicate: (String) -> Boolean, orElse: () -> Unit) {
        if (predicate(content)) return
        orElse()
    }

    override fun equals(other: Any?) =
        when (other) {
            is String -> content.equals(other, true)
            is Argument -> content == other.content && isFlag == other.isFlag
            else -> false
        } // Doesn't this break the symmetrical contract?

    override fun hashCode(): Int = content.lowercase().hashCode()

    override fun toString(): String {
        return "Argument(isFlag=$isFlag, content='$content')"
    }


}


