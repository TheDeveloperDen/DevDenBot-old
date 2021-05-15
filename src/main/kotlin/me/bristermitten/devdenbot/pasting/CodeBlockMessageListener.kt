package me.bristermitten.devdenbot.pasting

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.listener.EventListener
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import javax.inject.Inject

class CodeBlockMessageListener @Inject constructor(
    private val ddbConfig: DDBConfig,
) : EventListener {

    companion object {
        private val log by log()
        private const val MIN_ROWS_FOR_CONVERSION = 5

        internal val largeCodeBlock = Regex("```(?:[a-zA-Z]*\n)?((?:(?!.*```).*?\n){${MIN_ROWS_FOR_CONVERSION + 1},}.*?)```")
    }

    private suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) {
            return
        }
        val rawText = event.message.contentRaw
        if (rawText.startsWith("${ddbConfig.prefix}keep")) {
            return
        }
        if (!rawText.contains(largeCodeBlock)){
            return
        }
        val member = event.member ?: event.guild.retrieveMemberById(event.author.id).await()
        if (isAdmin(member) && !rawText.startsWith("${ddbConfig.prefix}convert")) {
            log.debug { "Admin messages that contain code blocks are not converted by default. Use the ${ddbConfig.prefix}convert prefix to automatically convert messages." }
            return
        }

        convertMessage(event.message, member)

    }

    internal suspend fun convertMessage(message: Message, author: Member) {
        var content = message.contentRaw
        val codeBlock = largeCodeBlock
            .findAll(content)
            .map { it.groupValues[1] }
            .map { scope.async { HasteClient.postCode(it) }}
            .toList()
            .awaitAll()
            .forEach{println(it)}

    }


    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>().launchEachIn(scope, this::onGuildMessageReceived)
    }

}