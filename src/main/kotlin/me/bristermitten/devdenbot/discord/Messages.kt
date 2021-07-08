package me.bristermitten.devdenbot.discord

import kotlinx.coroutines.delay
import me.bristermitten.devdenbot.extensions.await
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.restaction.MessageAction
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

suspend fun Message.fetchMember(): Member = member ?: guild.retrieveMemberById(author.id).await()

@OptIn(ExperimentalTime::class)
suspend fun MessageAction.awaitThenDelete(afterMillis: Duration = Duration.seconds(5)): Message {
    val message = this.await()
    delay(afterMillis)
    message.delete().await()
    return message
}
