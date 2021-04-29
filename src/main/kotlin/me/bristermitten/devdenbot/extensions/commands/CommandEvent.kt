package me.bristermitten.devdenbot.extensions.commands

import com.jagrosh.jdautilities.command.CommandEvent
import me.bristermitten.devdenbot.extensions.NUMERIC
import me.bristermitten.devdenbot.extensions.WHITESPACE_REGEX
import me.bristermitten.devdenbot.extensions.await
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @author Alexander Wood (BristerMitten)
 */

suspend inline fun CommandEvent.reply(function: (KotlinEmbedBuilder).() -> Unit): Message {
    val builder = KotlinEmbedBuilder().apply(function)

    return event.channel.sendMessage(builder.build()).await()
}

suspend inline fun CommandEvent.awaitReply(message: String): Message {
    return suspendCoroutine { cont ->
        this.reply(message, { cont.resume(it) }, { cont.resumeWithException(it) })
    }
}

inline fun CommandEvent.prepareReply(function: (KotlinEmbedBuilder).() -> Unit): MessageAction {
    val builder = KotlinEmbedBuilder().apply(function)

    return event.channel.sendMessage(builder.build())
}

fun CommandEvent.tempReply(message: String, cooldown: Int) {
    channel.sendMessage(message)
        .queue {
            it.delete().queueAfter(cooldown.toLong(), TimeUnit.SECONDS)
        }
}

val CommandEvent.firstMentionedUser: User?
    get() {
        if (message.mentionedUsers.isNotEmpty()) {
            return message.mentionedUsers.first()
        }
        return args.split(WHITESPACE_REGEX)
            .filter(String::isNotEmpty)
            .filter(NUMERIC::matches)
            .mapNotNull { jda.getUserById(it) }.firstOrNull()
    }
