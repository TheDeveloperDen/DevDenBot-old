package me.bristermitten.devdenbot.data

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

/**
 * @author AlexL
 */
object StatsUsers {

    private val users = ConcurrentHashMap<Long, StatsUser>()
    private val log by log()

    operator fun get(userId: Long): StatsUser {
        return users.getOrPut(userId) {
            StatsUser(userId)
        }
    }

    fun loadFrom(text: String) {
        val map = Json.decodeFromString<Map<Long, StatsUser>>(text)
        users.clear()
        users.putAll(map)
        log.debug("Loaded stats for ${users.size} users.")
    }

    fun saveToString(): String {
        return Json.encodeToString(MapSerializer(Long.serializer(), StatsUser.serializer()), users)
    }

    val all
        get() = users.values.toList()
}
