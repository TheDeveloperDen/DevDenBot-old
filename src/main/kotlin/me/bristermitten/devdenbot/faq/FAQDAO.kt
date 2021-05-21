package me.bristermitten.devdenbot.faq

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class FAQDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<FAQDAO>(FAQs)

    var author by FAQs.author
    var name by FAQs.name
    var title by FAQs.title
    var content by FAQs.content

    fun toPOJO() = FAQ(
        id.value,
        author = author,
        name = name,
        title = title,
        content = content
    )
}
