package me.bristermitten.devdenbot.faq

import org.jetbrains.exposed.dao.id.LongIdTable

object FAQs : LongIdTable() {
    val author = long("author")
    val name = varchar("name", 32).uniqueIndex()
    val title = varchar("title", 64)
    val content = text("content")
}
