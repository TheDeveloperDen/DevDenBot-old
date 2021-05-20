package me.bristermitten.devdenbot.data

import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable() {
    override val id = long("id").entityId() //We don't want autoincrement
    val xp = long("xp")
    val level = integer("level")
    val bumps = integer("level")

}
