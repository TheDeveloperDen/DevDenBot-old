package net.developerden.devdenbot.leaderboard

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

open class Leaderboard<T>(private val comparator: Comparator<T>) {
    private val mutex = Mutex()
    private val indices = ConcurrentHashMap<T, Int>()
    private val entries = ArrayList<T>()

    suspend fun addAll(entries: Collection<T>) = mutex.withLock {
        entries.filter { !indices.contains(it) }.forEach { this.entries.add(it) }
        this.entries.sortWith(comparator.reversed())
        indices.clear()
        this.entries.forEachIndexed { i, it -> indices[it] = i }
    }

    fun getEntryCount(): Int = indices.size

    fun getEntry(position: Int): T {
        return entries[position]
    }

    private fun getPosition(entry: T): Int? {
        return indices[entry]
    }

    /**
     * This function should **ALWAYS** be called under a lock with `mutex.withLock`
     */
    private fun add(entry: T) {
        if (!indices.containsKey(entry)) {
            indices[entry] = entries.size
            entries.add(entry)
        }
    }

    suspend fun update(entry: T) = mutex.withLock {
        if (!indices.containsKey(entry)) {
            add(entry)
        }

        var index = getPosition(entry) ?: throw IllegalStateException("User was not added correctly")
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
