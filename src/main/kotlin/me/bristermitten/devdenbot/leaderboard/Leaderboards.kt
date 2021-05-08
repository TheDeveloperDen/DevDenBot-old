package me.bristermitten.devdenbot.leaderboard

import me.bristermitten.devdenbot.data.StatsUser

object Leaderboards {

    val XP = Leaderboard<StatsUser>(compareBy { it.xp.get() })

}