package me.bristermitten.devdenbot.leaderboard

import me.bristermitten.devdenbot.data.StatsUser

object Leaderboards {

    val XP = StatsUserLeaderboard("XP") { it.xp.get() }
    val LEVEL = StatsUserLeaderboard("Level") { it.level.get() }
    val BUMPS = StatsUserLeaderboard("Bumps") { it.bumps.get() }

}


class StatsUserLeaderboard<T : Comparable<T>>(val name: String, val keyExtractor: (StatsUser) -> T) :
    Leaderboard<StatsUser>(Comparator.comparing(keyExtractor))