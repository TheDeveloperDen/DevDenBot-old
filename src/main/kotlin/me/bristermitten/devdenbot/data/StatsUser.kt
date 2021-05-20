package me.bristermitten.devdenbot.data

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * @author AlexL
 */
class StatsUser(
    id: EntityID<Long>,
) : LongEntity(id) {
    companion object : LongEntityClass<StatsUser>(Users)
    var xp by Users.xp
    var level by Users.level
    var bumps by Users.bumps

    val recentMessages = SafeCircularFifoQueue<CachedMessage>(10)
    var lastMessageSentTime: Long = -1

}
