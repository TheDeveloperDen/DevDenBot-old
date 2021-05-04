package me.bristermitten.devdenbot.data

import java.util.*

object MessageCache {

    private const val MAX_CACHE_SIZE = 1000;

    private val cache = mutableMapOf<Long, CachedMessage>()

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
