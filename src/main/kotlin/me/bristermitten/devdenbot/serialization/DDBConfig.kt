package me.bristermitten.devdenbot.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class DDBConfig(
    val token: String,
    val prefix: String,
    val ownerID: Long,
    val colour: Int,
    val commands: JsonObject
)
