@file:UseSerializers(BigIntegerSerializer::class)

package me.bristermitten.devdenbot.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import me.bristermitten.devdenbot.serialization.BigIntegerSerializer
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.math.BigInteger

/**
 * @author AlexL
 */
@Serializable
data class StatsUser(
    val userId: Long,
    var xp: BigInteger = BigInteger.ZERO,
    var level: Int = 0
) {
    @Transient
    val recentMessages = CircularFifoQueue<String>(5)

    var lastMessageSentTime: Long = -1
}
