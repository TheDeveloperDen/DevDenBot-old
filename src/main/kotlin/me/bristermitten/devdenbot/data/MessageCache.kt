package me.bristermitten.devdenbot.data

import java.util.*

object MessageCache {

    private const val MAX_CACHE_SIZE = 1000;

    private val cache = mutableMapOf<Long, CachedMessage>()
    private val byTime = PriorityQueue<CachedMessage>(compareBy { it.timestamp })

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
        getCached(id)?.msg = msg
    }

    fun getCached(id: Long): CachedMessage? {
        return cache[id]
    }

    val all get() = cache.toMap()
}
