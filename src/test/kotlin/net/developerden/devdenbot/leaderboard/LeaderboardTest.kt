package net.developerden.devdenbot.leaderboard

import kotlinx.coroutines.runBlocking
import java.util.Comparator.comparing
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LeaderboardTest {

    data class LeaderboardEntry(val id: Int, val xp: Long)

    private val users = (0..9).map { LeaderboardEntry(it, xp = (50 - 2 * it).toLong()) }


    @Test
    fun `Test addAll adds all entries correctly`() = runBlocking {
        val leaderboard = Leaderboard<LeaderboardEntry>(comparing { it.xp })
        leaderboard.addAll(users)

        assertEquals(10, leaderboard.getEntryCount())
        users.forEachIndexed { idx, user -> assertEquals(user, leaderboard.getEntry(idx)) }
    }

    @Test
    fun `Test update adds entry correctly`() = runBlocking {
        val leaderboard = Leaderboard<LeaderboardEntry>(comparing { it.xp })
        leaderboard.addAll(users)

        val newUser = LeaderboardEntry(4711, xp = 47L)
        leaderboard.update(newUser)
        assertEquals(11, leaderboard.getEntryCount())
        assertEquals(newUser, leaderboard.getEntry(2))
    }
}
