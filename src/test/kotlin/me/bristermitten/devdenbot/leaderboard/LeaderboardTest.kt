package me.bristermitten.devdenbot.leaderboard

import me.bristermitten.devdenbot.data.StatsUser
import me.bristermitten.devdenbot.util.atomic
import java.math.BigInteger
import java.util.Comparator.comparing
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LeaderboardTest {

    private val users = (0..9).map { StatsUser(it.toLong(), xp = BigInteger("${50 - 2 * it}").atomic()) }


    @Test
    fun `Test addAll adds all entries correctly`() {
        val leaderboard = Leaderboard<StatsUser>(comparing { it.xp })
        leaderboard.addAll(users);

        assertEquals(10, leaderboard.getEntryCount());
        users.forEachIndexed { idx, user -> assertEquals(user, leaderboard.getEntry(idx)) }
    }

    @Test
    fun `Test update adds entry correctly`() {
        val leaderboard = Leaderboard<StatsUser>(comparing { it.xp })
        leaderboard.addAll(users);

        val newUser = StatsUser(4711, xp = BigInteger("47").atomic())
        leaderboard.update(newUser)
        assertEquals(11, leaderboard.getEntryCount())
        assertEquals(newUser, leaderboard.getEntry(2))
    }
}