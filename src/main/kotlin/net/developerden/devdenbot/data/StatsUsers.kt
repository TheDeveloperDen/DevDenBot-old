package net.developerden.devdenbot.data

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * @author AlexL
 */
object StatsUsers {

    suspend fun get(userId: Long) = newSuspendedTransaction {
        StatsUserDAO.findById(userId) ?: StatsUserDAO.new(userId) {}
    }

    suspend fun all() = newSuspendedTransaction {
        StatsUserDAO.all().toList()
    }
}
