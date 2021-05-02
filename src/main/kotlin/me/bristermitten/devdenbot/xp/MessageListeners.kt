package me.bristermitten.devdenbot.xp

import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.util.botCommandsChannelId
import me.bristermitten.devdenbot.util.levenshtein
import net.dv8tion.jda.api.entities.Message

/**
 * @author AlexL
 */
private const val MESSAGE_MIN_DELAY_MILLIS = 750
private const val MIN_MESSAGE_LEN = 6
private const val MIN_DISTANCE = 0.4f

private fun similarityProportion(a: String, b: String) = levenshtein(a, b) / b.length.toDouble()

fun Message.shouldCountForStats(): Boolean {
    if (author.isBot) {
        return false
    }
    if (channel.idLong == botCommandsChannelId) {
        return false
    }
    val now = System.currentTimeMillis()
    val content = contentDisplay

    val len = content.length
    if (len < MIN_MESSAGE_LEN) {
        return false
    }

    val user = StatsUsers[author.idLong]
    if (now - user.lastMessageSentTime < MESSAGE_MIN_DELAY_MILLIS) {
        println("Message $content was discarded as $user typed ${now - user.lastMessageSentTime}ms ago")
        return false
    }

    if (content.none(Char::isLetter)) {
        return false
    }
    if (content.none(Char::isWhitespace)) {
        return false
    }
    if (user.recentMessages.any { similarityProportion(it, content) < MIN_DISTANCE }) {
        println("Message $content was discarded as it was too similar to previous messages - ${
            user.recentMessages.associateWith { similarityProportion(it, content) }
        }")
        return false
    }
    return true
}
