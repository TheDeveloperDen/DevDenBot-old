package me.bristermitten.devdenbot.data

import java.util.*
import kotlin.collections.HashMap

object MessageCache {

    private const val MAX_CACHE_SIZE = 1000;

    private val cache = HashMap<Long, CachedMessage>()
    private val byTime = PriorityQueue<CachedMessage>(Comparator.comparingLong {it.timestamp})

    @Synchronized
    fun cache(message: CachedMessage) {
        if (!cache.containsKey(message.id)) {
            byTime.add(message)
        }
        cache[message.id] = message
        if (cache.size > MAX_CACHE_SIZE) {
            cache.remove(byTime.poll().id)
        }
    }

    @Synchronized
    fun update(id: Long, msg: String) {
        getCached(id)?.also {
            it.msg = msg
        }
    }

    fun getCached(id: Long): CachedMessage? {
        return cache[id]
    }

}