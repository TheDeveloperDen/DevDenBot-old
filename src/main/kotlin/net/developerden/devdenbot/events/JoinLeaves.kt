package net.developerden.devdenbot.events

object JoinLeaves : EventTable() {
    val uid = long("user_id")
    val username = varchar("username", 37) //Their username + discriminator at the time of joining / leaving
    val type = enumerationByName("type", 5, Type::class)
    enum class Type {
        JOIN,
        LEAVE
    }
}
