package me.bristermitten.devdenbot.leaderboard

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.Comparator

class Leaderboard<T> (private val comparator: Comparator<T>) {

    private val indices = ConcurrentHashMap<T, Int>()
    private val entries = Vector<T>()

    fun addAll(entries: Collection<T>) {
        this.entries.addAll(entries)
        this.entries.sortWith(comparator)
        indices.clear()
        entries.forEachIndexed { i, it -> indices[it] = i }
    }

    fun getEntries(): Vector<T> {
        return entries
    }

    fun getPosition(entry: T): Int? {
        return indices[entry]
    }

    @Synchronized
    fun add(entry: T) {
        if (!indices.containsKey(entry)) {
            indices[entry] = entries.size
            entries.add(entry)
        }
        update(entry)
    }

    @Synchronized
    fun update(entry: T) {
        var index = getPosition(entry) ?: return
        while (index > 0 && comparator.compare(entry, entries[index - 1]) > 0) {
            val tmp = entries[index - 1]
            entries[index - 1] = entry
            entries[index] = tmp
            indices[entry] = index - 1
            indices[tmp] = index
            index--
        }
        while (index < entries.size - 1 && comparator.compare(entry, entries[index + 1]) < 0) {
            val tmp = entries[index + 1]
            entries[index + 1] = entry
            entries[index] = tmp
            indices[entry] = index + 1
            indices[tmp] = index
            index++
        }
    }

}
