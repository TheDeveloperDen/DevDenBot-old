package me.bristermitten.devdenbot.events

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime

abstract class EventTable : Table() {
    val id = integer("id").autoIncrement()
    val timestamp = datetime("timestamp")
    override val primaryKey = PrimaryKey(id)
}
