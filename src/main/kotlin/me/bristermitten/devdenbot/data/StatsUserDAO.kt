package me.bristermitten.devdenbot.data

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * @author AlexL
 */
class StatsUserDAO(
    id: EntityID<Long>,
) : LongEntity(id) {
    companion object : LongEntityClass<StatsUserDAO>(Users)

    // Accessing any of these properties requires a transaction!
    var xp by Users.xp
    var level by Users.level
    var bumps by Users.bumps

    suspend fun addXP(value: Int) = addXP(value.toLong())
    suspend fun addXP(value: Long) = newSuspendedTransaction {
        xp += value
    }

    val recentMessages = SafeCircularFifoQueue<CachedMessage>(10)
    var lastMessageSentTime: Long = -1

}
