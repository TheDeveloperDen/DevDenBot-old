package me.bristermitten.devdenbot.leaderboard

import me.bristermitten.devdenbot.data.StatsUser

object Leaderboards {

    val XP = Leaderboard<StatsUser> { o1, o2 -> o2.xp.get().compareTo(o1.xp.get()) }

}