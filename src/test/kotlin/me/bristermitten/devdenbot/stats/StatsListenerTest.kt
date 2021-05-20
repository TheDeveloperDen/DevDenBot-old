package me.bristermitten.devdenbot.stats

import kotlin.test.Test
import kotlin.test.assertEquals

internal class StatsListenerTest{

    @Test
    fun `onGuildMessageReceives increments messages stat` () {
        val cut = StatsListener()
        assertEquals(0, GlobalStats.totalMessagesSent.get().toInt())
        cut.onGuildMessageReceived()
        assertEquals(1, GlobalStats.totalMessagesSent.get().toInt())
    }
}