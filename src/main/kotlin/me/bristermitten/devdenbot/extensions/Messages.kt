package me.bristermitten.devdenbot.extensions

import com.jagrosh.jdautilities.command.CommandEvent

val WHITESPACE_REGEX = Regex("\\s+")

fun CommandEvent.arguments(): Arguments {
    val split = this.message.contentRaw.split(WHITESPACE_REGEX)
    val args = split.drop(1).map(::Argument)
    return Arguments(
        split.first() //This might break in future for prefixes that don't require a space. deal with it
        , args.filter { !it.isFlag }, args.filter { it.isFlag })
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

    override fun equals(other: Any?) = if (other is String) content.equals(other, true) else content == other
    override fun hashCode(): Int = content.lowercase().hashCode()
}


