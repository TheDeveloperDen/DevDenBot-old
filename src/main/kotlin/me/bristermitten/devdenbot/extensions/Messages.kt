package me.bristermitten.devdenbot.extensions

import com.jagrosh.jdautilities.command.CommandEvent

val WHITESPACE_REGEX = Regex("\\s+")
val NUMERIC = Regex("[0-9]+")

fun CommandEvent.arguments(): Arguments {
    val split = this.message.contentRaw.split(WHITESPACE_REGEX)
    val args = split.subList(1, split.size).map(::Argument)
    return Arguments(split[0].removePrefix(split[0].first().toString()), args)
}


class Arguments(val command: String, private val args: List<Argument>) : List<Argument> by args {

    fun validate(index: Int, predicate: (String) -> Boolean, orElse: () -> Unit) {
        val s = args[index]
        s.validate(predicate, orElse)
    }

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

    inline fun validate(boolean: Boolean, orElse: () -> Unit) {
        validate({ boolean }, orElse)
    }

    inline fun validateInList(vararg list: String, orElse: () -> Unit) {
        validate(list.contains(content), orElse)
    }

    override fun equals(other: Any?) = if (other is String) content.equals(other, true) else content == other
    override fun hashCode(): Int = content.hashCode()
}


