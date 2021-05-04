package me.bristermitten.devdenbot.data

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

object MessageCache {

    private const val MAX_CACHE_SIZE = 1000;

    private val cache = ConcurrentHashMap<Long, CachedMessage>()

    @Synchronized
    fun cache(message: CachedMessage) {
        cache[message.id] = message
        if (cache.size > MAX_CACHE_SIZE) {
            cache.remove(cache.keys.iterator().next())
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
