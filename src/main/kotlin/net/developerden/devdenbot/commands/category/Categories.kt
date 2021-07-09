package net.developerden.devdenbot.commands.category

import net.developerden.devdenbot.util.log
import java.util.*

/**
 * @author Alexander Wood (BristerMitten)
 */
object Categories : Iterable<CommandCategory> {

    private val categories = TreeSet(compareBy(CommandCategory::getName))
    private val log by log()

    fun register(commandCategory: CommandCategory) {
        categories.add(commandCategory)
        log.trace("Registered CommandCategory ${commandCategory.name}")
    }

    fun byName(name: String) = categories.firstOrNull {
        it.name.equals(name, true) || it.shortName.equals(name, true)
    }

    override fun iterator() = categories.iterator()
}
