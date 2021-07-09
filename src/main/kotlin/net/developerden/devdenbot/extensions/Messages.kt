package net.developerden.devdenbot.extensions

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import kotlin.coroutines.resume


val WHITESPACE_REGEX = Regex("\\s+")

suspend fun Message.getMentionedMember(index: Int): Member? {
    val str = contentRaw.split(WHITESPACE_REGEX).getOrNull(index) ?: return null
    val id = str.toLongOrNull()
    if (id != null) {
        return (guild.retrieveMemberById(id.toString(), false).await { cont, _ ->
            cont.resume(null)
        })
    }
    val first = mentionedMembers.getOrNull(index) ?: return null
    if (str.contains(first.id)) {
        return first
    }
    return null
}
