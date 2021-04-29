package me.bristermitten.devdenbot.commands.category

import java.util.*

/**
 * @author Alexander Wood (BristerMitten)
 */
object Categories : Iterable<CommandCategory> {

    private val categories = TreeSet(Comparator.comparing(CommandCategory::getName))

    fun register(commandCategory: CommandCategory) {
        categories.add(commandCategory)
    }

    fun byName(name: String) = categories.firstOrNull {
        it.name.equals(name, true) || it.shortName.equals(name, true)
    }

    override fun iterator() = categories.iterator()
}
