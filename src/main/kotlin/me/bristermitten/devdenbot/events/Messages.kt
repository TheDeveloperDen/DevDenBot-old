package me.bristermitten.devdenbot.events

object Messages : EventTable() {
    val uid = long("user_id")
    val channelId = long("channel_id")
    val content = text("content")
    val type = enumerationByName("type", 10, Type::class)

    enum class Type {
        CREATE,
        EDIT, // The new content of the message should be stored, as the old one could be extracted from the previous create / edit event
        DELETE
    }
}
