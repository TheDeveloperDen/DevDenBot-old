package me.bristermitten.devdenbot.sql

data class SQLConfig(
    val hostname: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
)
