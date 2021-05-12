package me.bristermitten.devdenbot.leaderboard

import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers
import java.math.BigInteger

object Leaderboards {

    val XP = StatsUserLeaderboard("XP") { it.xp.get() }
    val LEVEL = StatsUserLeaderboard("Level") { it.level.get() }
    val BUMPS = StatsUserLeaderboard("Bumps") { it.bumps.get() }

    fun initializeLeaderboards() {
        val users = StatsUsers.all
        XP.addAll(users.filter { it.xp.get() != BigInteger.ZERO })
        LEVEL.addAll(users.filter { it.level.get() != 0 })
        BUMPS.addAll(users.filter { it.bumps.get() != 0 })
    }
} 

class StatsUserLeaderboard<T : Comparable<T>>(val name: String, val keyExtractor: (StatsUser) -> T) :
    Leaderboard<StatsUser>(Comparator.comparing(keyExtractor))