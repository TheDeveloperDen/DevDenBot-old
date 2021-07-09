package net.developerden.devdenbot.commands.arguments

data class Arguments(val command: String, val args: List<Argument>, val flags: List<Argument>) {

    inline fun validateArgLength(length: Int, orElse: () -> Unit) {
        if (args.size < length)
            orElse()
    }

}


class Argument(rawContent: String) {
    val isFlag = rawContent.startsWith('-')
    val content = rawContent.removePrefix("-")

    inline fun validate(predicate: (String) -> Boolean, orElse: () -> Unit) {
        if (predicate(content)) return
        orElse()
    }

    override fun equals(other: Any?) =
        when (other) {
            is String -> content.equals(other, true)
            is Argument -> content == other.content && isFlag == other.isFlag
            else -> false
        } // Doesn't this break the symmetrical contract? // yes this is bad and should be fixed

    override fun hashCode(): Int = content.lowercase().hashCode()

    override fun toString(): String {
        return "Argument(isFlag=$isFlag, content='$content')"
    }


}

