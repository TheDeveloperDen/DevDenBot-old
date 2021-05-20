package me.bristermitten.devdenbot.data

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * @author AlexL
 */
object StatsUsers {

    suspend fun get(userId: Long) = newSuspendedTransaction {
        StatsUser.findById(userId) ?: StatsUser.new(userId) {
            this.bumps = 0
            this.level = 0
        }
    }

    suspend fun all() = newSuspendedTransaction {
        StatsUser.all()
    }
}
