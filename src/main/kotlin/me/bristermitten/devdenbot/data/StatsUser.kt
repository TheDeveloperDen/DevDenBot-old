@file:UseSerializers(BigIntegerSerializer::class, AtomicIntegerSerializer::class)

package me.bristermitten.devdenbot.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import me.bristermitten.devdenbot.leaderboard.Leaderboards
import me.bristermitten.devdenbot.serialization.AtomicIntegerSerializer
import me.bristermitten.devdenbot.serialization.BigIntegerSerializer
import me.bristermitten.devdenbot.serialization.PrettyName
import me.bristermitten.devdenbot.util.atomic
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author AlexL
 */
@Serializable
data class StatsUser(
    val userId: Long,
    @PrettyName("XP")
    var xp: AtomicBigInteger = AtomicBigInteger(BigInteger.ZERO),
    var level: AtomicInteger = 0.atomic(),
    var bumps: AtomicInteger = 0.atomic(),
) {
    @Transient
    val recentMessages = SafeCircularFifoQueue<CachedMessage>(10)
    var lastMessageSentTime: Long = -1

    fun giveXP(amount: BigInteger) {
        this.xp += amount
        Leaderboards.XP.update(this)
    }

    fun incrementLevel(): Int {
        Leaderboards.LEVEL.update(this)
        return this.level.incrementAndGet()
    }

    fun incrementBumps(): Int {
        Leaderboards.BUMPS.update(this)
        return this.bumps.incrementAndGet()
    }

}
