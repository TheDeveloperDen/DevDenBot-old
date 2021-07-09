package net.developerden.devdenbot.data

data class CachedMessage(val id: Long, val authorId: Long, val timestamp: Long, var msg: String)
