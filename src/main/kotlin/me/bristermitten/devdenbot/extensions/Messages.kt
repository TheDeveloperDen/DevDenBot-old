package me.bristermitten.devdenbot.extensions

import com.jagrosh.jdautilities.command.CommandEvent

val WHITESPACE_REGEX = Regex("\\s+")

fun CommandEvent.arguments(): Arguments {
    val split = this.message.contentRaw.split(WHITESPACE_REGEX)
    val args = split.drop(1).map(::Argument)
    return Arguments(
        split.first() //This might break in future for prefixes that don't require a space. deal with it
        , args)
}


class Arguments(val command: String, private val args: List<Argument>) : List<Argument> by args {

    inline fun validateLength(length: Int, orElse: () -> Unit) {
        if (size < length)
            orElse()
    }

    override fun toString(): String {
        return "Arguments(command='$command', args=$args)"
    }

}


class Argument(val content: String) {

    inline fun validate(predicate: (String) -> Boolean, orElse: () -> Unit) {
        if (predicate(content)) return
        orElse()
    }

    override fun equals(other: Any?) = if (other is String) content.equals(other, true) else content == other
    override fun hashCode(): Int = content.hashCode()
}


