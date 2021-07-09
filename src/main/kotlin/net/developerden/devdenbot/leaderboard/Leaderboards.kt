package net.developerden.devdenbot.leaderboard

import net.developerden.devdenbot.data.StatsUserDAO
import net.developerden.devdenbot.data.StatsUsers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object Leaderboards {

    val XP = StatsUserLeaderboard("XP") { it.xp }
    val LEVEL = StatsUserLeaderboard("Level") { it.level }
    val BUMPS = StatsUserLeaderboard("Bumps") { it.bumps }

    suspend fun initializeLeaderboards() = newSuspendedTransaction {
        val users = StatsUsers.all()
        XP.addAll(users.filter { it.xp != 0L })
        LEVEL.addAll(users.filter { it.level != 0 })
        BUMPS.addAll(users.filter { it.bumps != 0 })
    }
}

class StatsUserLeaderboard<T : Comparable<T>>(val name: String, val keyExtractor: (StatsUserDAO) -> T) :
    Leaderboard<StatsUserDAO>(Comparator.comparing(keyExtractor))
