package me.bristermitten.devdenbot.pasting

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import me.bristermitten.devdenbot.discord.BOT_COMMANDS_CHANNEL_ID
import me.bristermitten.devdenbot.extensions.await
import me.bristermitten.devdenbot.extensions.commands.KotlinEmbedBuilder
import me.bristermitten.devdenbot.listener.EventListener
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.Instant
import javax.inject.Inject

class CodeBlockMessageListener @Inject constructor(
    private val ddbConfig: DDBConfig,
) : EventListener {

    companion object {
        private val log by log()
        private const val MIN_ROWS_FOR_CONVERSION = 10

        internal val codeBlock = Regex("```(?:(?<lang>[a-zA-Z]+)?\\n)?((?:.|\\n)*?)```")

    }

    private suspend fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) {
            return
        }
        if (event.channel.idLong == BOT_COMMANDS_CHANNEL_ID) {
            return
        }
        val rawText = event.message.contentRaw
        if (rawText.startsWith("${ddbConfig.prefix}keep")) {
            return
        }
        if (!rawText.contains(codeBlock)) {
            return
        }
        val member = event.member ?: event.guild.retrieveMemberById(event.author.id).await()
        if (isAdmin(member) && !rawText.startsWith("${ddbConfig.prefix}convert")) {
            log.debug { "Admin messages that contain code blocks are not converted by default. Use the ${ddbConfig.prefix}convert prefix to automatically convert messages." }
            return
        }

        val embed = createEmbed(event.message, member) ?: return

        event.channel.sendMessage(embed).queue()
        event.message.delete().queue()

    }

    private suspend fun createEmbed(message: Message, member: Member): MessageEmbed? {
        var index = 0
        val urls = codeBlock.findAll(message.contentRaw)
            .map { it.groups.last()?.value }
            .filter { it?.lines()?.size ?: 0 >= MIN_ROWS_FOR_CONVERSION }
            .filterNotNull()
            .map { scope.async { HasteClient.postCode(it) } }
            .toList()
            .awaitAll()

        if (urls.isEmpty()) { // this is a bit hacky
            return null
        }

        val dc = codeBlock.replace(message.contentRaw) {
            if (it.groups.last()?.value?.lines()?.size ?: 0 >= MIN_ROWS_FOR_CONVERSION) {
                urls[index].also { index++ }
            } else {
                it.value
            }
        }
            .removePrefix("${ddbConfig.prefix}convert")
            .trim()

        val embedBuilder = KotlinEmbedBuilder().apply {
            author = member.effectiveName
            authorImage = member.user.effectiveAvatarUrl
            color = Color(ddbConfig.colour)
            description = dc
            setTimestamp(Instant.now())

            setFooter("This message was converted automatically to keep the channels clean from large code blocks.")

        }

        return embedBuilder.build()


    }


    override fun register(jda: JDA) {
        jda.listenFlow<GuildMessageReceivedEvent>().handleEachIn(scope, this::onGuildMessageReceived)
    }

}
