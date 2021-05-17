package me.bristermitten.devdenbot.events

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime

object Events : Table() {
    val id = integer("id").autoIncrement()
    val action = enumerationByName("event_type", 32, EventType::class)
    val timestamp = datetime("timestamp")
    override val primaryKey = PrimaryKey(id)
}
