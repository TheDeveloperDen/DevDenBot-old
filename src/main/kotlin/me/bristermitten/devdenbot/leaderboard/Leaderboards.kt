package me.bristermitten.devdenbot.leaderboard

import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.data.StatsUsers

object Leaderboards {

    val XP = StatsUserLeaderboard("XP") { it.xp.get() }
    val LEVEL = StatsUserLeaderboard("Level") { it.level.get() }
    val BUMPS = StatsUserLeaderboard("Bumps") { it.bumps.get() }

    fun initializeLeaderboards() {
        val users = StatsUsers.all
        XP.addAll(users)
        LEVEL.addAll(users)
        BUMPS.addAll(users)
    }
}

class StatsUserLeaderboard<T : Comparable<T>>(val name: String, val keyExtractor: (StatsUser) -> T) :
    Leaderboard<StatsUser>(Comparator.comparing(keyExtractor))