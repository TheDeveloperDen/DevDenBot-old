package net.developerden.devdenbot.data

import org.jetbrains.exposed.dao.id.IdTable

object Users : IdTable<Long>() {
    override val id = long("id").entityId() //We don't want autoincrement
    val xp = long("xp").default(0)
    val level = integer("level").default(0)
    val bumps = integer("bumps").default(0)

    override val primaryKey = PrimaryKey(id)
}
