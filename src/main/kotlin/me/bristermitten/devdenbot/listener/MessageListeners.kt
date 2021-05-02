package me.bristermitten.devdenbot.listener

import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.util.botCommandsChannelId
import me.bristermitten.devdenbot.util.levenshtein
import net.dv8tion.jda.api.entities.Message

/**
 * @author AlexL
 */
private const val MESSAGE_MIN_DELAY_MILLIS = 750
private const val MIN_MESSAGE_LEN = 6
private const val MAX_SIMILARITY = 0.5f

private fun similarityProportion(a: String, b: String) = levenshtein(a, b) / b.length

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
        return false
    }

    if (content.none(Char::isLetter)) {
        return false
    }
    if (content.none(Char::isWhitespace)) {
        return false
    }

    if (user.recentMessages.any { similarityProportion(it, content) > MAX_SIMILARITY }) {
        return false
    }
    return true
}
