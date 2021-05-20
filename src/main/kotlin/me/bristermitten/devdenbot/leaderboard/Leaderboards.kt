package me.bristermitten.devdenbot.leaderboard

import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers
import java.math.BigInteger

object Leaderboards {

    val XP = StatsUserLeaderboard("XP") { it.xp }
    val LEVEL = StatsUserLeaderboard("Level") { it.level }
    val BUMPS = StatsUserLeaderboard("Bumps") { it.bumps }

    suspend fun initializeLeaderboards() {
        val users = StatsUsers.all()
        XP.addAll(users.filter { it.xp != 0L })
        LEVEL.addAll(users.filter { it.level != 0 })
        BUMPS.addAll(users.filter { it.bumps != 0 })
    }
}

class StatsUserLeaderboard<T : Comparable<T>>(val name: String, val keyExtractor: (StatsUser) -> T) :
    Leaderboard<StatsUser>(Comparator.comparing(keyExtractor))
