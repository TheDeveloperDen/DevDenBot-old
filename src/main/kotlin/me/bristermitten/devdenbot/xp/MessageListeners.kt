package me.bristermitten.devdenbot.xp

import me.bristermitten.devdenbot.data.StatsUserDAO
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.discord.BOT_COMMANDS_CHANNEL_ID
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.levenshtein
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User

/**
 * @author AlexL
 */
private const val MESSAGE_MIN_DELAY_MILLIS = 750
private const val MIN_MESSAGE_LEN = 6
private const val MIN_DISTANCE = 0.4f

fun similarityProportion(a: String, b: String) = levenshtein(a, b) / b.length.toDouble()

private val log = KotlinLogging.logger("MessageListeners")

suspend fun shouldCountForStats(author: User, content: String, channel: MessageChannel, config: DDBConfig): Boolean {
    if (content.startsWith(config.prefix)) {
        return false
    }
    if (author.isBot) {
        return false
    }
    if (channel.idLong == BOT_COMMANDS_CHANNEL_ID) {
        return false
    }
    val now = System.currentTimeMillis()

    val len = content.length
    if (len < MIN_MESSAGE_LEN) {
        return false
    }

    val user = StatsUsers.get(author.idLong)
    if (now - user.lastMessageSentTime < MESSAGE_MIN_DELAY_MILLIS) {
        log.info { "Message $content was discarded as $user typed ${now - user.lastMessageSentTime}ms ago" }
        return false
    }

    if (content.none(Char::isLetter)) {
        return false
    }
    if (content.none(Char::isWhitespace)) {
        return false
    }
    return true
}

fun isTooSimilar(userDAO: StatsUserDAO, content: String): Boolean =
    userDAO.recentMessages.copy().any { similarityProportion(it.msg, content) < MIN_DISTANCE }

