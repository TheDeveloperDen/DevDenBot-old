package me.bristermitten.devdenbot.sql

import java.sql.Connection

interface SQLSerializable {
    suspend fun load(connection: Connection)
    suspend fun save(connection: Connection)
}
