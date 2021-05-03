@file:UseSerializers(BigIntegerSerializer::class)
package me.bristermitten.devdenbot.stats

import kotlinx.serialization.UseSerializers
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import me.bristermitten.devdenbot.serialization.BigIntegerSerializer
import java.math.BigInteger

@Serializable
object GlobalStats {
    //Actual thread safety is effort
    @get:Synchronized
    @set:Synchronized
    var totalMessagesSent: BigInteger = BigInteger.ZERO

    @get:Synchronized
    @set:Synchronized
    var xpGiven: BigInteger = BigInteger.ZERO

    @get:Synchronized
    @set:Synchronized
    var levelUps: BigInteger = BigInteger.ZERO

    fun saveToString(): String {
        return Json.encodeToString(serializer(), this)
    }

    fun loadFrom(json: String) {
        val loaded = Json.decodeFromString<GlobalStats>(json)
        this.totalMessagesSent = loaded.totalMessagesSent
        this.xpGiven = loaded.xpGiven
        this.levelUps = loaded.levelUps
    }
}
