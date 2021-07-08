package me.bristermitten.devdenbot.extensions.commands

import com.jagrosh.jdautilities.command.CommandEvent
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import me.bristermitten.devdenbot.commands.arguments.Argument
import me.bristermitten.devdenbot.commands.arguments.arguments
import me.bristermitten.devdenbot.discord.awaitThenDelete
import me.bristermitten.devdenbot.extensions.await
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.restaction.MessageAction
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * @author Alexander Wood (BristerMitten)
 */


suspend inline fun CommandEvent.awaitReply(message: String): Message {
    return suspendCoroutine { cont ->
        this.reply(message, { cont.resume(it) }, { cont.resumeWithException(it) })
    }
}

inline fun CommandEvent.prepareReply(function: (KotlinEmbedBuilder).() -> Unit): MessageAction {
    val builder = KotlinEmbedBuilder().apply(function)

    return event.channel.sendMessageEmbeds(builder.build())
}

@OptIn(ExperimentalTime::class)
suspend fun CommandEvent.tempReply(message: String, cooldown: Duration = Duration.seconds(5)) =
    channel.sendMessage(message)
        .awaitThenDelete(cooldown)

@OptIn(ExperimentalTime::class)
suspend fun CommandEvent.tempReply(message: MessageEmbed, cooldown: Duration = Duration.seconds(5)) =
    channel.sendMessageEmbeds(message)
        .awaitThenDelete(cooldown)

@OptIn(ExperimentalTime::class)
suspend fun CommandEvent.tempReply(message: Collection<MessageEmbed>, cooldown: Duration = Duration.seconds(5)) =
    channel.sendMessageEmbeds(message)
        .awaitThenDelete(cooldown)

suspend fun CommandEvent.firstMentionedUser(): User? {
    if (message.mentionedUsers.isNotEmpty()) {
        return message.mentionedUsers.first()
    }
    return arguments()
        .args
        .asSequence()
        .map(Argument::content)
        .mapNotNull(String::toLongOrNull)
        .asFlow()
        .mapNotNull { jda.retrieveUserById(it).await() }
        .firstOrNull()
}
