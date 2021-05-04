@file:UseSerializers(BigIntegerSerializer::class, AtomicIntegerSerializer::class)

package me.bristermitten.devdenbot.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import me.bristermitten.devdenbot.serialization.AtomicIntegerSerializer
import me.bristermitten.devdenbot.serialization.BigIntegerSerializer
import me.bristermitten.devdenbot.serialization.PrettyName
import me.bristermitten.devdenbot.stats.GlobalStats
import me.bristermitten.devdenbot.util.atomic
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.math.BigInteger
import java.util.*
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
        GlobalStats.xpGiven += amount
    }

}
