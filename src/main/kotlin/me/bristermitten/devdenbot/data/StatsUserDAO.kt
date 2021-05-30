package me.bristermitten.devdenbot.data

import me.bristermitten.devdenbot.leaderboard.Leaderboards
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
        private set
    var level by Users.level
        private set
    var bumps by Users.bumps
        private set

    suspend fun setXP(value: Long) =
        newSuspendedTransaction {
            xp = value
            Leaderboards.XP.update(this@StatsUserDAO)
        }

    suspend fun addXP(value: Long) = setXP(xp + value)

    suspend fun setLevel(value: Int) = newSuspendedTransaction {
        level = value
        Leaderboards.LEVEL.update(this@StatsUserDAO)
    }

    suspend fun setBumps(value: Int) = newSuspendedTransaction {
        bumps = value
        Leaderboards.BUMPS.update(this@StatsUserDAO)
    }

    val recentMessages = SafeCircularFifoQueue<CachedMessage>(10)
    var lastMessageSentTime = -1L

    override fun equals(other: Any?): Boolean {
        return other is StatsUserDAO && other.id == this.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
