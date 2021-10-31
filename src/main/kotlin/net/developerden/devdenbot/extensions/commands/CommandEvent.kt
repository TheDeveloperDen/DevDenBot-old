package net.developerden.devdenbot.extensions.commands

import com.jagrosh.jdautilities.command.CommandEvent
import net.developerden.devdenbot.discord.awaitThenDelete
import net.developerden.devdenbot.extensions.await
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

@ExperimentalTime
suspend fun CommandEvent.tempReply(message: String, cooldown: Duration = Duration.seconds(5)) =
    channel.sendMessage(message)
        .awaitThenDelete(cooldown)

@ExperimentalTime
suspend fun CommandEvent.tempReply(message: MessageEmbed, cooldown: Duration = Duration.seconds(5)) =
    channel.sendMessageEmbeds(message)
        .awaitThenDelete(cooldown)

@OptIn(ExperimentalTime::class)
suspend fun CommandEvent.tempReply(message: Collection<MessageEmbed>, cooldown: Duration = Duration.seconds(5)) =
    channel.sendMessageEmbeds(message)
        .awaitThenDelete(cooldown)


val mentionRegex = Regex("""<@!?(\d+)>""")
val idRegex = Regex("""\d{17,}""")

suspend fun CommandEvent.getUser(): User? {
    return if (args.isBlank()) event.author else
        mentionRegex.find(args)?.groups?.get(1)
            ?.let {
                guild.retrieveMemberById(it.value, false).await()
            }?.user
            ?: idRegex.find(args)?.groups?.get(1)
                ?.let {
                    guild.retrieveMemberById(it.value, false).await()
                }?.user
            ?: guild.retrieveMembersByPrefix(args, 1).await()
                .firstOrNull()
                ?.takeIf { it.user.name == args || it.nickname == args }
                ?.user

}
