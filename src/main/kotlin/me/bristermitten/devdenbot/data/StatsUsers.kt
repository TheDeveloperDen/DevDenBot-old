package me.bristermitten.devdenbot.data

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * @author AlexL
 */
object StatsUsers {

    suspend fun get(userId: Long) = newSuspendedTransaction {
        StatsUser.findById(userId) ?: StatsUser.new(userId) {}
    }

    suspend fun all() = newSuspendedTransaction {
        StatsUser.all().toList()
    }
}
