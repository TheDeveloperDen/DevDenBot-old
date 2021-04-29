package me.bristermitten.devdenbot.extensions.commands

import com.jagrosh.jdautilities.command.CommandEvent
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import me.bristermitten.devdenbot.extensions.Argument
import me.bristermitten.devdenbot.extensions.arguments
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

suspend fun CommandEvent.firstMentionedUser(): User? {
    if (message.mentionedUsers.isNotEmpty()) {
        return message.mentionedUsers.first()
    }
    return arguments()
        .asSequence()
        .map(Argument::content)
        .mapNotNull(String::toLongOrNull)
        .asFlow()
        .mapNotNull { jda.retrieveUserById(it).await() }
        .firstOrNull()
}
