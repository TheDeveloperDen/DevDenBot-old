package me.bristermitten.devdenbot.data

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

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

    suspend fun addXP(value: Int) = addXP(value.toLong())
    suspend fun addXP(value: Long) = newSuspendedTransaction {
        xp += value
    }

    suspend fun setBumps(newBumps: Int) = newSuspendedTransaction {
        bumps = newBumps
    }

    suspend fun setLevel(newLevel: Int) = newSuspendedTransaction {
        level = newLevel
    }

    val recentMessages = SafeCircularFifoQueue<CachedMessage>(10)
    var lastMessageSentTime: Long = -1

}
